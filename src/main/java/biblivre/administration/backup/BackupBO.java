/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.administration.backup;

import biblivre.core.AbstractBO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.file.BiblivreFile;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.utils.*;
import biblivre.core.utils.PgDumpCommand.Format;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.digitalmedia.DigitalMediaDTO;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BackupBO extends AbstractBO {
    private BackupDAO backupDAO;
    private DigitalMediaBO digitalMediaBO;
    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;

    public void simpleBackup() throws BackupException {
        BackupType backupType = BackupType.FULL;
        BackupScope backupScope = this.getBackupScope(SchemaThreadLocal.get());

        ArrayList<String> list = new ArrayList<>();
        list.add(Constants.GLOBAL_SCHEMA);

        String schema = SchemaThreadLocal.get();

        if (Constants.GLOBAL_SCHEMA.equals(schema)) {
            list.addAll(schemaBO.getEnabledSchemasList());
        } else {
            list.add(schema);
        }

        Map<String, Pair<String, String>> map = new HashMap<>();

        for (String s : list) {
            if (schemaBO.isNotLoaded(s)) {
                continue;
            }

            String title = configurationBO.getString(Constants.CONFIG_TITLE);
            String subtitle = configurationBO.getString(Constants.CONFIG_SUBTITLE);
            map.put(s, Pair.of(title, subtitle));
        }

        BackupDTO dto = this.prepare(map, backupType, backupScope);
        this.backup(dto);
    }

    public BackupScope getBackupScope(String schema) {
        if (Constants.GLOBAL_SCHEMA.equals(schema)) {
            return BackupScope.MULTI_SCHEMA;
        } else if (configurationBO.isMultipleSchemasEnabled()) {
            return BackupScope.SINGLE_SCHEMA_FROM_MULTI_SCHEMA;
        } else {
            return BackupScope.SINGLE_SCHEMA;
        }
    }

    public BackupDTO prepare(
            Map<String, Pair<String, String>> schemas, BackupType type, BackupScope scope) {
        BackupDTO dto = new BackupDTO(schemas, type, scope);
        dto.setCurrentStep(0);

        int steps;
        int schemasCount = dto.getSchemas().size();

        steps =
                switch (dto.getType()) {
                    case FULL ->
                            // schema, data and media for each schema (except media for public) +
                            // zip
                            schemasCount * 3;
                    case EXCLUDE_DIGITAL_MEDIA ->
                            // schema and data for each schema + zip
                            (schemasCount * 2) + 1;
                    case DIGITAL_MEDIA_ONLY ->
                            // media for each schema (except for public) + zip
                            schemasCount;
                };

        dto.setSteps(steps);

        if (this.save(dto)) {
            return dto;
        } else {
            return null;
        }
    }

    public void backup(BackupDTO dto) throws BackupException {
        this.createBackup(dto);

        if (dto.getBackup() != null) {
            this.move(dto);
        }
    }

    public void createBackup(BackupDTO dto) throws BackupException {
        if (pgDumpExecutable() == null) {
            throw new BackupException("pg_dump not found");
        }

        File tmpDir = null;

        try {
            tmpDir = Files.createTempDirectory("biblivre_backup").toFile();

            Map<String, Pair<String, String>> schemas = dto.getSchemas();
            BackupType type = dto.getType();

            // Writing metadata
            writeBackupMetadata(dto, tmpDir);

            for (String schema : schemas.keySet()) {
                if (type != BackupType.DIGITAL_MEDIA_ONLY) {
                    dumpSchema(dto, tmpDir, schema);

                    dumpData(dto, tmpDir, schema);
                }

                if (shouldDumpMedia(schema, type)) {
                    dumpMedia(dto, tmpDir, schema);
                }
            }

            File tmpZip = new File(tmpDir.getAbsolutePath() + ".b5bz");

            FileIOUtils.zipFolder(tmpDir, tmpZip);

            dto.increaseCurrentStep();
            this.save(dto);

            dto.setBackup(tmpZip);
        } catch (IOException e) {
            throw new BackupException("can't create temp dir", e);
        } finally {
            if (tmpDir != null) {
                FileUtils.deleteQuietly(tmpDir);
            }
        }
    }

    File pgDumpExecutable() {
        return DatabaseUtils.getPgDump();
    }

    private static boolean shouldDumpMedia(String schema, BackupType type) {
        return !Constants.GLOBAL_SCHEMA.equals(schema) && type == BackupType.FULL
                || type == BackupType.DIGITAL_MEDIA_ONLY;
    }

    private static void writeBackupMetadata(BackupDTO dto, File tmpDir) throws IOException {
        File meta = new File(tmpDir, "backup.meta");

        try (Writer writer =
                FileWriterWithEncoding.builder()
                        .setFile(meta)
                        .setCharset(Constants.DEFAULT_CHARSET)
                        .get()) {

            writer.write(new RestoreOperation(dto).toJSONString());
        }
    }

    public BackupDTO get(Integer id) {
        return this.backupDAO.get(id);
    }

    public List<BackupDTO> list() {
        return this.backupDAO.list();
    }

    public BackupDTO getLastBackup() {
        List<BackupDTO> list = this.backupDAO.list(1);

        if (list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

    public boolean save(BackupDTO dto) {
        return this.backupDAO.save(dto);
    }

    public void move(BackupDTO dto) throws BackupException {
        File destination = this.getServerBackupDestination();
        File backup = dto.getBackup();

        if (destination == null) {
            destination = backup.getParentFile();
        }

        StringBuilder sb = new StringBuilder();

        // Format: Biblivre Backup 2012-09-08 12h01m22s Full.b5bz
        Formatter formatter = new Formatter(sb);
        formatter.format(
                "Biblivre Backup %1$tY-%1$tm-%1$td %1$tHh%1$tMm%1$tSs %2$s.b5bz",
                new Date(), StringUtils.capitalize(dto.getType().toString()));
        formatter.close();

        File movedBackup = new File(destination, sb.toString());

        if (!backup.renameTo(movedBackup)) {
            throw new BackupException("can't move backup to " + movedBackup.getAbsolutePath());
        }

        dto.setBackup(movedBackup);
        this.save(dto);
    }

    public String getBackupPath() {
        String path = configurationBO.getString(Constants.CONFIG_BACKUP_PATH);

        if (StringUtils.isBlank(path) || FileIOUtils.doesNotExists(path)) {
            File home = new File(System.getProperty("user.home"));
            File biblivre = new File(home, "Biblivre");

            if (createBiblivreDirectoryIfNeeded(biblivre, home)) {
                return null;
            }

            path = biblivre.getAbsolutePath();
        }

        return path;
    }

    public File getServerBackupDestination() {
        String path = this.getBackupPath();

        return FileIOUtils.getWritablePath(path);
    }

    void exportDigitalMedia(String schema, File path) throws BackupException {
        List<DigitalMediaDTO> list = digitalMediaBO.list();

        for (DigitalMediaDTO dto : list) {
            BiblivreFile file = digitalMediaBO.load(dto.getId(), dto.getName());

            if (file == null) {
                throw new BackupException(
                        "digital media "
                                + dto.getId()
                                + " ("
                                + dto.getName()
                                + ") was not found in schema "
                                + schema);
            }

            File destination =
                    new File(
                            path,
                            dto.getId()
                                    + "_"
                                    + TextUtils.removeNonLettersOrDigits(dto.getName(), "-"));

            try (file;
                    OutputStream writer = Files.newOutputStream(destination.toPath())) {
                file.copy(writer);
            } catch (IOException ioException) {
                throw new BackupException(
                        "can't export digital media " + dto.getId() + " from schema " + schema,
                        ioException);
            }
        }
    }

    void dumpDatabase(ProcessBuilder processBuilder) throws BackupException {
        processBuilder.redirectErrorStream(true);

        Process process;

        try {
            process = processBuilder.start();
        } catch (IOException ioException) {
            throw new BackupException("can't start pg_dump", ioException);
        }

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> output = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                output.add(line);

                if (log.isDebugEnabled()) {
                    log.debug(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new BackupException(pgDumpFailure(exitCode, output));
            }
        } catch (IOException ioException) {
            throw new BackupException("can't read pg_dump output", ioException);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new BackupException("pg_dump was interrupted", interruptedException);
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    private static String pgDumpFailure(int exitCode, List<String> output) {
        String message = "pg_dump exited with status " + exitCode;

        if (output.isEmpty()) {
            return message;
        }

        return message + ": " + String.join(System.lineSeparator(), output);
    }

    private void dumpDatabase(PgDumpCommand command) throws BackupException {
        this.dumpDatabase(new ProcessBuilder(command.getCommands()));
    }

    private void dump(
            BackupDTO dto,
            String schema,
            File backupFile,
            boolean isSchemaOnly,
            boolean isDataOnly,
            String excludeTablePattern,
            String includeTablePattern)
            throws BackupException {

        File pgdump = pgDumpExecutable();

        if (pgdump == null) {
            throw new BackupException("pg_dump not found");
        }

        InetSocketAddress defaultAddress =
                new InetSocketAddress(
                        DatabaseUtils.getDatabaseHostName(),
                        Integer.parseInt(DatabaseUtils.getDatabasePort()));

        Format defaultFormat = Format.PLAIN;

        this.dumpDatabase(
                new PgDumpCommand(
                        pgdump,
                        defaultAddress,
                        Constants.DEFAULT_CHARSET,
                        defaultFormat,
                        schema,
                        backupFile,
                        isSchemaOnly,
                        isDataOnly,
                        excludeTablePattern,
                        includeTablePattern));

        dto.increaseCurrentStep();
        this.save(dto);
    }

    private void dumpData(BackupDTO dto, File tmpDir, String schema) throws BackupException {
        File dataBackup = new File(tmpDir, schema + ".data.b5b");
        boolean isSchemaOnly = false;
        boolean isDataOnly = true;
        String excludeTablePattern = schema + ".digital_media";

        dump(dto, schema, dataBackup, isSchemaOnly, isDataOnly, excludeTablePattern, null);
    }

    private void dumpSchema(BackupDTO dto, File tmpDir, String schema) throws BackupException {
        File schemaBackup = new File(tmpDir, schema + ".schema.b5b");
        boolean isSchemaOnly = true;
        boolean isDataOnly = false;

        dump(dto, schema, schemaBackup, isSchemaOnly, isDataOnly, null, null);
    }

    private void dumpMedia(BackupDTO dto, File tmpDir, String schema) throws BackupException {
        File mediaBackup = new File(tmpDir, schema + ".media.b5b");
        boolean isSchemaOnly = false;
        boolean isDataOnly = true;
        String includeTablePattern = schema + ".digital_media";

        dump(dto, schema, mediaBackup, isSchemaOnly, isDataOnly, null, includeTablePattern);

        File schemaBackup = new File(tmpDir, schema);

        if (!schemaBackup.mkdir()) {
            throw new BackupException("can't create schema backup directory " + schemaBackup);
        }

        this.exportDigitalMedia(schema, schemaBackup);
        this.save(dto);
    }

    private static boolean createBiblivreDirectoryIfNeeded(File biblivre, File home) {
        return !biblivre.exists() && home.isDirectory() && home.canWrite() && !biblivre.mkdir();
    }

    public Set<String> listDatabaseSchemas() {
        return backupDAO.listDatabaseSchemas();
    }

    @Autowired
    public void setBackupDAO(BackupDAO backupDAO) {
        this.backupDAO = backupDAO;
    }

    @Autowired
    public void setDigitalMediaBO(DigitalMediaBO digitalMediaBO) {
        this.digitalMediaBO = digitalMediaBO;
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}

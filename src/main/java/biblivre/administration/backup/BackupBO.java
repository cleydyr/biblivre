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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BackupBO extends AbstractBO {
    private BackupDAO backupDAO;
    private DigitalMediaBO digitalMediaBO;
    private SchemaBO schemaBO;
    private ConfigurationBO configurationBO;

    public void simpleBackup() {
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

    public void backup(BackupDTO dto) {
        try {
            this.createBackup(dto);

            if (dto.getBackup() != null) {
                this.move(dto);
            }
        } catch (Exception e) {
            logger.error("error while creating backup", e);
        }
    }

    public void createBackup(BackupDTO dto) throws BackupException {
        File pgdump = DatabaseUtils.getPgDump();

        if (pgdump == null) {
            return;
        }

        try {
            File tmpDir = Files.createTempDirectory("biblivre_backup").toFile();

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

            FileUtils.deleteQuietly(tmpDir);

            dto.increaseCurrentStep();
            this.save(dto);

            dto.setBackup(tmpZip);
        } catch (IOException e) {
            throw new BackupException("can't create temp dir", e);
        }
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

    public void move(BackupDTO dto) {
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

        boolean success = backup.renameTo(movedBackup);

        if (success) {
            dto.setBackup(movedBackup);
        }

        this.save(dto);
    }

    public String getBackupPath() {
        String path = configurationBO.getString(Constants.CONFIG_BACKUP_PATH);

        if (StringUtils.isBlank(path) || FileIOUtils.doesNotExists(path)) {
            File home = new File(System.getProperty("user.home"));
            File biblivre = new File(home, "Biblivre");

            if (!biblivre.exists() && home.isDirectory() && home.canWrite()) {
                if (!biblivre.mkdir()) {
                    return null;
                }
            }

            path = biblivre.getAbsolutePath();
        }

        return path;
    }

    public File getServerBackupDestination() {
        String path = this.getBackupPath();

        return FileIOUtils.getWritablePath(path);
    }

    private void exportDigitalMedia(String schema, File path) {
        List<DigitalMediaDTO> list = digitalMediaBO.list();

        for (DigitalMediaDTO dto : list) {
            BiblivreFile file = digitalMediaBO.load(dto.getId(), dto.getName());
            File destination =
                    new File(
                            path,
                            dto.getId()
                                    + "_"
                                    + TextUtils.removeNonLettersOrDigits(dto.getName(), "-"));
            try (OutputStream writer = Files.newOutputStream(destination.toPath())) {
                file.copy(writer);

                file.close();
            } catch (IOException ioException) {
                logger.error("error while exporting digital media", ioException);

                return;
            }
        }
    }

    private void dumpDatabase(ProcessBuilder pb) {
        pb.redirectErrorStream(true);

        try {
            Process p = pb.start();

            try (InputStreamReader isr = new InputStreamReader(p.getInputStream());
                    BufferedReader br = new BufferedReader(isr)) {
                String line;

                while ((line = br.readLine()) != null) {
                    // There was a system.out.println here for the 'line' var,
                    // with a FIX_ME tag.  So I changed it to logger.debug().
                    if (logger.isDebugEnabled()) {
                        logger.debug(line);
                    }
                }

                p.waitFor();

                p.exitValue();
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void dumpDatabase(PgDumpCommand command) {
        this.dumpDatabase(new ProcessBuilder(command.getCommands()));
    }

    private void dump(
            BackupDTO dto,
            String schema,
            File backupFile,
            boolean isSchemaOnly,
            boolean isDataOnly,
            String excludeTablePattern,
            String includeTablePattern) {

        File pgdump = DatabaseUtils.getPgDump();

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

    private void dumpData(BackupDTO dto, File tmpDir, String schema) {
        File dataBackup = new File(tmpDir, schema + ".data.b5b");
        boolean isSchemaOnly = false;
        boolean isDataOnly = true;
        String excludeTablePattern = schema + ".digital_media";
        String includeTablePattern = null;

        dump(dto, schema, dataBackup, isSchemaOnly, isDataOnly, excludeTablePattern, null);
    }

    private void dumpSchema(BackupDTO dto, File tmpDir, String schema) {
        File schemaBackup = new File(tmpDir, schema + ".schema.b5b");
        boolean isSchemaOnly = true;
        boolean isDataOnly = false;
        String excludeTablePattern = null;
        String includeTablePattern = null;

        dump(dto, schema, schemaBackup, isSchemaOnly, isDataOnly, null, null);
    }

    private void dumpMedia(BackupDTO dto, File tmpDir, String schema) throws BackupException {
        File mediaBackup = new File(tmpDir, schema + ".media.b5b");
        boolean isSchemaOnly = false;
        boolean isDataOnly = true;
        String excludeTablePattern = null;
        String includeTablePattern = schema + ".digital_media";

        dump(dto, schema, mediaBackup, isSchemaOnly, isDataOnly, null, includeTablePattern);

        File schemaBackup = new File(tmpDir, schema);

        if (!schemaBackup.mkdir()) {
            throw new BackupException("can't create schema backup directory " + schemaBackup);
        }

        this.exportDigitalMedia(schema, schemaBackup);
        this.save(dto);
    }

    protected static final Logger logger = LoggerFactory.getLogger(BackupBO.class);

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

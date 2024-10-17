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

import static biblivre.core.SchemaThreadLocal.withGlobalSchema;
import static biblivre.core.SchemaThreadLocal.withSchema;

import biblivre.administration.backup.exception.RestoreException;
import biblivre.administration.setup.State;
import biblivre.core.Updates;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestoreBO {

    private static final String[] ALLOWED_BACKUP_EXTENSIONS = new String[] {"b4bz", "b5bz"};

    private static final Logger logger = LoggerFactory.getLogger(RestoreBO.class);

    private BackupBO backupBO;

    private RestoreService restoreService;

    /**
     * List all the backup files in the server backup destination.
     *
     * @return a list of RestoreOperation objects.
     * @throws RestoreException if the backup files' integrity is compromised.
     */
    public List<RestoreOperation> list() throws RestoreException {
        File serverBackupDestionation = getActualServerBackupDestionation();

        List<RestoreOperation> list = new ArrayList<>();

        for (File backupFile :
                FileUtils.listFiles(serverBackupDestionation, ALLOWED_BACKUP_EXTENSIONS, false)) {
            RestoreOperation restoreOperation = RestoreBO.fromBackupMetadata(backupFile);

            if (restoreOperation.isValid()) {
                list.add(restoreOperation);
            }
        }

        sortRestores(list);

        return list;
    }

    /**
     * Restore a backup file.
     *
     * @param restoreOperation the backup file to be restored.
     * @param mediaRestoreOperation the media backup file to be restored.
     * @return true if the restore was successful, false otherwise.
     * @throws RestoreException if the backup file is corrupted.
     */
    public boolean restore(
            RestoreOperation restoreOperation, RestoreOperation mediaRestoreOperation)
            throws RestoreException {
        if (isInvalid(restoreOperation)) {
            throw new ValidationException(
                    "administration.maintenance.backup.error." + "corrupted_backup_file");
        }

        try {
            File tempExplodedBackup = FileIOUtils.unzip(restoreOperation.getBackup());

            String extension = getExtension(restoreOperation);

            if (mediaRestoreOperation != null) {
                movePartialFiles(mediaRestoreOperation, tempExplodedBackup);
            }

            countRestoreSteps(restoreOperation, tempExplodedBackup, extension);

            boolean restoreBackup = restoreBackup(restoreOperation, tempExplodedBackup);

            FileUtils.deleteQuietly(tempExplodedBackup);

            tryUpdates();

            return restoreBackup;
        } catch (IOException e) {
            throw new RestoreException(e);
        }
    }

    /**
     * Get a restore operation from a backup file.
     *
     * @param filename the backup file name.
     * @return a RestoreOperation object from the backup file.
     * @throws RestoreException if the backup file is not found.
     */
    public RestoreOperation getRestoreOperation(String filename) throws RestoreException {
        File serverBackupDestionation = getActualServerBackupDestionation();

        File backup = new File(serverBackupDestionation, filename);

        if (!backup.exists()) {
            throw new RestoreException(
                    "administration.maintenance.backup.error.backup_file_not_found");
        }

        return fromBackupMetadata(backup);
    }

    private File getActualServerBackupDestionation() {
        File path = backupBO.getServerBackupDestination();

        if (path == null) {
            path = FileUtils.getTempDirectory();
        }
        return path;
    }

    @Autowired
    public void setBackupBO(BackupBO backupBO) {
        this.backupBO = backupBO;
    }

    private synchronized boolean restoreBackup(RestoreOperation dto, File explodedBackupDirectory)
            throws RestoreException {
        Map<String, String> restoreSchemas = dto.getRestoreSchemas();

        validateRestoreSchemas(restoreSchemas);

        RestoreContextHelper context =
                new RestoreContextHelper(dto, backupBO.listDatabaseSchemas());

        String globalSchema = Constants.GLOBAL_SCHEMA;

        State.writeLog("Starting psql");

        restoreService.processSchemaRenames(context.getPreRenameSchemas());

        String extension = getExtension(dto);

        if (restoreSchemas.containsKey(globalSchema)) {
            processGlobalSchema(explodedBackupDirectory, extension);
        }

        for (String schema : restoreSchemas.keySet()) {
            if (globalSchema.equals(schema)) {
                continue;
            }

            withSchema(
                    schema,
                    () ->
                            restoreService.processSchemaRestores(
                                    explodedBackupDirectory, extension, schema));
        }

        restoreService.processSchemaRenames(context.getPostRenameSchemas());

        restoreService.processSchemaRenames(context.getRestoreRenamedSchemas());

        postProcessDeletes(context.getDeleteSchemas());

        postProcessRenames(dto, restoreSchemas);

        restoreService.removeNonExistingPGSchemaFromSchemasTable();

        logger.info("===== Restoring backup finished =====");

        return true;
    }

    private void processGlobalSchema(File directory, String extension) throws RestoreException {
        State.writeLog("Processing schema for 'global'");

        File ddlFile = new File(directory, "global.schema." + extension);

        withGlobalSchema(() -> restoreService.processRestore(ddlFile));

        State.writeLog("Processing data for 'global'");

        File dmlFile = new File(directory, "global.data." + extension);

        withGlobalSchema(() -> restoreService.processRestore(dmlFile));
    }

    private void postProcessRenames(
            RestoreOperation restoreOperation, Map<String, String> restoreSchemas)
            throws RestoreException {

        for (Map.Entry<String, String> entry : restoreSchemas.entrySet()) {
            String targetSchemaName = entry.getValue();

            if (Constants.GLOBAL_SCHEMA.equals(targetSchemaName)) {
                continue;
            }

            String sourceSchemaName = entry.getKey();

            String escapedSchemaTitle = getEscapedSchemaTitle(restoreOperation, sourceSchemaName);

            restoreService.deleteSchemaFromSchemasTable(sourceSchemaName);

            restoreService.addSchemaToSchemaTable(targetSchemaName, escapedSchemaTitle);
        }
    }

    private static String getEscapedSchemaTitle(
            RestoreOperation restoreOperation, String sourceSchemaName) {
        String schemaTitle = restoreOperation.getSchemas().get(sourceSchemaName).getLeft();

        // Escape single quotes and backslashes
        return schemaTitle.replaceAll("'", "''").replaceAll("\\\\", "\\\\");
    }

    private void postProcessDeletes(Map<String, String> deleteSchemas) throws RestoreException {
        for (Map.Entry<String, String> entry : deleteSchemas.entrySet()) {
            String originalSchemaName = entry.getKey();

            String schemaToBeDeleted = entry.getValue();

            State.writeLog("Droping schema " + schemaToBeDeleted);

            String globalSchema = Constants.GLOBAL_SCHEMA;

            if (!globalSchema.equals(originalSchemaName)) {
                restoreService.deleteAllDigitalMedia(schemaToBeDeleted);
            }

            restoreService.dropSchema(schemaToBeDeleted);

            if (!globalSchema.equals(originalSchemaName)) {
                restoreService.deleteSchemaFromSchemasTable(originalSchemaName);
            }
        }
    }

    private static void validateRestoreSchemas(Map<String, String> restoreSchemas) {
        if (restoreSchemas != null && !restoreSchemas.isEmpty()) {
            return;
        }

        throw new ValidationException("administration.maintenance.backup.error.no_schema_selected");
    }

    /**
     * Get a restore operation from a backup file.
     *
     * @param backup the backup file.
     * @return a RestoreOperation object from the backup file based on the contents of the
     *     backup.meta file inside the backup.
     * @throws RestoreException if the backup file can't be read.
     */
    private static RestoreOperation fromBackupMetadata(File backup) throws RestoreException {
        try (ZipFile zipFile = new ZipFile(backup)) {
            ZipEntry metadata = zipFile.getEntry("backup.meta");

            if (metadata == null) {
                throw new RestoreException("Can't read metadata from zip file");
            }

            try (InputStream content = zipFile.getInputStream(metadata)) {
                JSONObject json = getJsonObject(content);

                RestoreOperation dto = new RestoreOperation(json);

                dto.setBackup(backup);

                return dto;
            }
        } catch (IOException ioException) {
            logger.error("Can't read zip file", ioException);

            throw new RestoreException(ioException);
        }
    }

    private static JSONObject getJsonObject(InputStream content) throws IOException {
        StringWriter writer = new StringWriter();

        IOUtils.copy(content, writer, StandardCharsets.UTF_8);

        return new JSONObject(writer.toString());
    }

    private static String buildUpdateDigitalMediaQuery(long oid, long newOid) {
        return "UPDATE digital_media SET blob = '%d' WHERE blob = '%d';".formatted(newOid, oid);
    }

    private static void countRestoreSteps(RestoreOperation dto, File tmpDir, String extension)
            throws RestoreException {
        long steps = 0;

        for (String schema : dto.getRestoreSchemas().keySet()) {
            steps += getSteps(tmpDir, schema + ".schema." + extension);

            steps += getSteps(tmpDir, schema + ".data." + extension);

            if (!Constants.GLOBAL_SCHEMA.equals(schema)) {
                steps += getSteps(tmpDir, schema + ".media." + extension);

                steps += RestoreService.getStepsFromMediaFolder(tmpDir, schema);
            }
        }

        State.setSteps(steps);
        State.writeLog(
                "Restoring "
                        + dto.getRestoreSchemas().size()
                        + " schemas for a total of "
                        + steps
                        + " SQL lines");
    }

    private static long getSteps(File tmpDir, String fileName) throws RestoreException {
        try (Stream<String> lines = Files.lines(new File(tmpDir, fileName).toPath())) {
            return lines.count();
        } catch (IOException e) {
            throw new RestoreException(e);
        }
    }

    private static void movePartialFiles(RestoreOperation partial, File tmpDir) {
        try {
            File explodedBackupDir = FileIOUtils.unzip(partial.getBackup());

            for (File partialFile : Objects.requireNonNull(explodedBackupDir.listFiles())) {
                if ("backup.meta".equals(partialFile.getName())) {
                    FileUtils.deleteQuietly(partialFile);
                } else if (partialFile.isDirectory()) {
                    FileUtils.moveDirectoryToDirectory(partialFile, tmpDir, true);
                } else {
                    FileUtils.moveFileToDirectory(partialFile, tmpDir, true);
                }
            }

            FileUtils.deleteQuietly(explodedBackupDir);
        } catch (IOException ioException) {
            throw new ValidationException(
                    "administration.maintenance.backup.error.couldnt_unzip_backup", ioException);
        }
    }

    private static void sortRestores(List<RestoreOperation> list) {
        list.sort(
                (restore1, restore2) -> {
                    if (restore2 == null) {
                        return -1;
                    }

                    if (restore1.getCreated() != null && restore2.getCreated() != null) {
                        return restore2.getCreated().compareTo(restore1.getCreated()); // Order Desc
                    }

                    if (restore1.getBackup() != null && restore2.getBackup() != null) {
                        return restore1.getBackup()
                                .getName()
                                .compareTo(restore2.getBackup().getName());
                    }

                    return 0;
                });
    }

    private static boolean isInvalid(RestoreOperation dto) {
        return !dto.isValid() || dto.getBackup() == null || !dto.getBackup().exists();
    }

    private static String getExtension(RestoreOperation dto) {
        return dto.getBackup().getPath().endsWith("b5bz") ? "b5b" : "b4b";
    }

    private Updates updatesSuite;

    private SchemaDAO schemaDAO;

    public void tryUpdates() {
        updatesSuite.globalUpdate();

        for (SchemaDTO schema : schemaDAO.list()) {
            updatesSuite.schemaUpdate(schema.getSchema());
        }
    }

    @Autowired
    public void setUpdates(Updates updateSuite) {
        this.updatesSuite = updateSuite;
    }

    @Autowired
    public void setSchemaDAO(SchemaDAO schemaDAO) {
        this.schemaDAO = schemaDAO;
    }

    @Autowired
    public void setRestoreService(RestoreService restoreService) {
        this.restoreService = restoreService;
    }
}

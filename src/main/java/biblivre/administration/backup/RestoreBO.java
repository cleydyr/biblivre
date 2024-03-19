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
import biblivre.core.UpdatesDAO;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.schemas.SchemaDAO;
import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.CharPool;
import biblivre.core.utils.Constants;
import biblivre.core.utils.FileIOUtils;
import biblivre.database.util.PostgreSQLStatementIterable;
import biblivre.digitalmedia.DigitalMediaDAO;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestoreBO {
    private static final String[] FILTERED_OUT_STATEMENT_PREFIXES =
            new String[] {"CREATE FUNCTION", "ALTER FUNCTION", "CREATE TRIGGER"};

    private static final Pattern DIGITAL_MEDIA_FILE_PATTERN = Pattern.compile("^(\\d+)_(.*)$");
    private static final Pattern LO_OPEN_COMMAND_PATTERN =
            Pattern.compile("(.*lo_open\\(')(.*?)(',.*)");

    private static final Pattern LO_CREATE_COMMAND_PATTERN =
            Pattern.compile("lo_create\\('(.*?)'\\)");

    private static final String[] ALLOWED_BACKUP_EXTENSIONS = new String[] {"b4bz", "b5bz"};

    private static final Logger logger = LoggerFactory.getLogger(RestoreBO.class);

    private DigitalMediaDAO digitalMediaDAO;

    private BackupBO backupBO;

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

    public static void processRestore(File restore) throws RestoreException {

        if (restore == null) {
            logger.info("===== Skipping File 'null' =====");
            return;
        }

        if (!restore.exists()) {
            logger.info(
                    "===== Skipping File '{}' =====",
                    restore.getName()); // NOPMD - suppressed GuardLogStatement - TODO explain
            // reason for suppression
            return;
        }

        logger.info("===== Restoring File '{}' =====", restore.getName());

        try (BufferedReader bufferedReader = Files.newBufferedReader(restore.toPath())) {
            ObservableIterator<Character> sourceIterator =
                    new ObservableIterator<>(
                            new BufferedReaderIterator(bufferedReader),
                            c -> {
                                if (c == CharPool.NEW_LINE) {
                                    State.incrementCurrentStep();
                                }
                            });

            PostgreSQLStatementIterable postgreSQLStatementIterable =
                    new PostgreSQLStatementIterable(sourceIterator);

            for (String statement : postgreSQLStatementIterable) {
                if (isFunctionOrTriggerRelated(statement)) {
                    continue;
                }

                if (statement.startsWith("COPY ")) {
                    executePostgresCopyCommand(statement, sourceIterator);

                    continue;
                }

                writeLine(statement);
            }
        } catch (IOException e) {
            throw new RestoreException(e);
        }
    }

    private static void executePostgresCopyCommand(
            String copyCommand, Iterator<Character> sourceIterator) throws RestoreException {
        UpdatesDAO updatesDAO = UpdatesDAO.getInstance();

        sourceIterator.next(); // Skip the first character (new line) of the COPY command

        try (Connection connection = updatesDAO.beginUpdate()) {
            PGConnection pgConnection = connection.unwrap(PGConnection.class);

            CopyManager copyManager = pgConnection.getCopyAPI();

            copyManager.copyIn(copyCommand, new PostgresCopyDataReader(sourceIterator));
        } catch (SQLException | IOException e) {
            throw new RestoreException(e);
        }
    }

    public static boolean isFunctionOrTriggerRelated(String statement) {
        return Arrays.stream(FILTERED_OUT_STATEMENT_PREFIXES).anyMatch(statement::startsWith);
    }

    @Autowired
    public void setDigitalMediaDAO(DigitalMediaDAO digitalMediaDAO) {
        this.digitalMediaDAO = digitalMediaDAO;
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

        processSchemaRenames(context.getPreRenameSchemas());

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
                    () -> processSchemaRestores(explodedBackupDirectory, extension, schema));
        }

        processSchemaRenames(context.getPostRenameSchemas());

        processSchemaRenames(context.getRestoreRenamedSchemas());

        postProcessDeletes(context.getDeleteSchemas());

        postProcessRenames(dto, restoreSchemas);

        removeNonExistingPGSchemaFromSchemasTable();

        writeLine("ANALYZE");

        logger.info("===== Restoring backup finished =====");

        return true;
    }

    private static void removeNonExistingPGSchemaFromSchemasTable() throws RestoreException {
        writeLine(
                """
            DELETE FROM "global".schemas WHERE "schema" not in (SELECT schema_name FROM information_schema.schemata)
            """);
    }

    private void processGlobalSchema(File directory, String extension) throws RestoreException {
        State.writeLog("Processing schema for 'global'");

        File ddlFile = new File(directory, "global.schema." + extension);

        withGlobalSchema(() -> processRestore(ddlFile));

        State.writeLog("Processing data for 'global'");

        File dmlFile = new File(directory, "global.data." + extension);

        withGlobalSchema(() -> processRestore(dmlFile));
    }

    private static void postProcessRenames(
            RestoreOperation restoreOperation, Map<String, String> restoreSchemas)
            throws RestoreException {

        for (Map.Entry<String, String> entry : restoreSchemas.entrySet()) {
            String targetSchemaName = entry.getValue();

            if (Constants.GLOBAL_SCHEMA.equals(targetSchemaName)) {
                continue;
            }

            String sourceSchemaName = entry.getKey();

            String escapedSchemaTitle = getEscapedSchemaTitle(restoreOperation, sourceSchemaName);

            deleteSchemaFromSchemasTable(sourceSchemaName);

            addSchemaToSchemaTable(targetSchemaName, escapedSchemaTitle);
        }
    }

    private static void addSchemaToSchemaTable(String targetSchemaName, String escapedSchemaTitle)
            throws RestoreException {
        writeLine(buildInsertSchemaQuery(targetSchemaName, escapedSchemaTitle));
    }

    private static String getEscapedSchemaTitle(
            RestoreOperation restoreOperation, String sourceSchemaName) {
        String schemaTitle = restoreOperation.getSchemas().get(sourceSchemaName).getLeft();

        // Escape single quotes and backslashes
        return schemaTitle.replaceAll("'", "''").replaceAll("\\\\", "\\\\");
    }

    private static void postProcessDeletes(Map<String, String> deleteSchemas)
            throws RestoreException {
        for (Map.Entry<String, String> entry : deleteSchemas.entrySet()) {
            String originalSchemaName = entry.getKey();

            String schemaToBeDeleted = entry.getValue();

            State.writeLog("Droping schema " + schemaToBeDeleted);

            String globalSchema = Constants.GLOBAL_SCHEMA;

            if (!globalSchema.equals(originalSchemaName)) {
                deleteAllDigitalMedia(schemaToBeDeleted);
            }

            dropSchema(schemaToBeDeleted);

            if (!globalSchema.equals(originalSchemaName)) {
                deleteSchemaFromSchemasTable(originalSchemaName);
            }
        }
    }

    private static void deleteSchemaFromSchemasTable(String originalSchemaName)
            throws RestoreException {
        writeLine(
                """
            DELETE FROM "global".schemas WHERE "schema" = '%s'
            """
                        .formatted(originalSchemaName));
    }

    private static void dropSchema(String schemaToBeDeleted) throws RestoreException {
        writeLine(
                """
            DROP SCHEMA "%s" CASCADE
            """
                        .formatted(schemaToBeDeleted));
    }

    private static void deleteAllDigitalMedia(String schemaToBeDeleted) throws RestoreException {
        writeLine(
                """
            DELETE FROM "%s".digital_media
            """
                        .formatted(schemaToBeDeleted));
    }

    private static String buildInsertSchemaQuery(String finalSchemaName, String schemaTitle) {
        return """
            INSERT INTO "global".schemas (schema, name) VALUES ('%s', E'%s');
            """
                .formatted(finalSchemaName, schemaTitle);
    }

    private void processSchemaRestores(File path, String extension, String schema)
            throws RestoreException {

        State.writeLog("Processing schema for '" + schema + "'");

        processRestore(new File(path, schema + ".schema." + extension));

        State.writeLog("Processing data for '" + schema + "'");

        processRestore(new File(path, schema + ".data." + extension));

        State.writeLog("Processing media for '" + schema + "'");

        this.processMediaRestore(new File(path, schema + ".media." + extension), schema);
        this.processMediaRestoreFolder(new File(path, schema));
    }

    private static void processSchemaRenames(Map<String, String> preRenameSchemas)
            throws RestoreException {
        for (Map.Entry<String, String> entry : preRenameSchemas.entrySet()) {
            String originalSchemaName = entry.getKey();

            String finalSchemaName = entry.getValue();

            State.writeLog(
                    "Renaming schema %s to %s".formatted(originalSchemaName, finalSchemaName));

            changePGSchemaName(originalSchemaName, finalSchemaName);
        }
    }

    private static void changePGSchemaName(String originalSchemaName, String finalSchemaName)
            throws RestoreException {
        writeLine(
                """
                ALTER SCHEMA "%s" RENAME TO "%s"
                """
                        .formatted(originalSchemaName, finalSchemaName));
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
            ZipArchiveEntry metadata = zipFile.getEntry("backup.meta");

            if (metadata == null || !zipFile.canReadEntryData(metadata)) {
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

    private void processMediaRestoreFolder(File path) throws RestoreException {
        if (path == null) {
            logger.info("===== Skipping File 'null' =====");
            return;
        }

        if (!path.exists() || !path.isDirectory()) {
            logger.info("===== Skipping File '{}' =====", path.getName());
            return;
        }

        for (File file : Objects.requireNonNull(path.listFiles())) {
            Matcher fileMatcher = DIGITAL_MEDIA_FILE_PATTERN.matcher(file.getName());

            if (!fileMatcher.find()) {
                continue;
            }

            logger.info("===== Restoring File '{}' =====", file.getName());

            String mediaId = fileMatcher.group(1);

            long oid = digitalMediaDAO.importFile(file);

            State.incrementCurrentStep();

            String newLine = buildUpdateDigitalMediaQuery(mediaId, oid);

            writeLine(newLine);
        }
    }

    private String buildUpdateDigitalMediaQuery(String mediaId, long oid) {
        return "UPDATE digital_media SET blob = '%d' WHERE id = '%s';".formatted(oid, mediaId);
    }

    private void processMediaRestore(File restore, String schema) throws RestoreException {

        if (restore == null) {
            logger.info("===== Skipping File 'null' =====");
            return;
        }

        if (!restore.exists()) {
            logger.info("===== Skipping File '{}' =====", restore.getName());
            return;
        }

        logger.info("===== Restoring File '{}' =====", restore.getName());

        try (BufferedReader bufferedReader = Files.newBufferedReader(restore.toPath())) {
            ObservableIterator<Character> sourceIterator =
                    new ObservableIterator<>(
                            new BufferedReaderIterator(bufferedReader),
                            (c) -> {
                                if (c == CharPool.NEW_LINE) {
                                    State.incrementCurrentStep();
                                }
                            });

            PostgreSQLStatementIterable postgreSQLStatementIterable =
                    new PostgreSQLStatementIterable(sourceIterator);

            Map<Long, Long> oidMap = new HashMap<>();

            for (String statement : postgreSQLStatementIterable) {
                if (statement.startsWith("COPY ")) {
                    executePostgresCopyCommand(statement, sourceIterator);

                    continue;
                }

                processLOLine(statement, oidMap, sourceIterator);
            }

            setPGSearchPath(schema);

            for (Map.Entry<Long, Long> entry : oidMap.entrySet()) {
                String query = buildUpdateDigitalMediaQuery(entry.getKey(), entry.getValue());

                writeLine(query);
            }
        } catch (IOException e) {
            throw new RestoreException(e);
        }
    }

    private static void setPGSearchPath(String schema) throws RestoreException {
        writeLine(
                """
            SET search_path = "%s", pg_catalog
            """
                        .formatted(schema));
    }

    private void processLOLine(
            String line, Map<Long, Long> oidMap, Iterator<Character> sourceIterator)
            throws RestoreException {

        if (line.startsWith("SELECT pg_catalog.lo_create")) {
            processNewOid(line, oidMap);
        } else if (line.startsWith("SELECT pg_catalog.lo_open")) {
            processsOpenOid(line, oidMap);
        } else if (!ignoreLine(line)) {
            if (line.startsWith("COPY")) {
                executePostgresCopyCommand(line, sourceIterator);

                logger.info(line);

                return;
            }

            writeLine(line);
        }
    }

    private static void processsOpenOid(String line, Map<Long, Long> oidMap)
            throws RestoreException {
        Matcher loOpenMatcher = LO_OPEN_COMMAND_PATTERN.matcher(line);

        if (!loOpenMatcher.find()) {
            return;
        }

        Long oid = Long.valueOf(loOpenMatcher.group(2));

        String newLine = loOpenMatcher.replaceFirst("$1" + oidMap.get(oid) + "$3");

        writeLine(newLine);
    }

    private void processNewOid(String line, Map<Long, Long> oidMap) {
        Matcher loCreateMatcher = LO_CREATE_COMMAND_PATTERN.matcher(line);

        if (loCreateMatcher.find()) {
            Long currentOid = Long.valueOf(loCreateMatcher.group(1));

            Long newOid = digitalMediaDAO.createOID();

            logger.info("Creating new OID (old: {}, new: {})", currentOid, newOid);

            oidMap.put(currentOid, newOid);
        }
    }

    private static boolean ignoreLine(String line) {
        return line.startsWith("ALTER LARGE OBJECT")
                || line.startsWith("BEGIN;")
                || line.startsWith("COMMIT;");
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

                steps += getStepsFromMediaFolder(tmpDir, schema);
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

    private static long getStepsFromMediaFolder(File tmpDir, String schema) {
        File mediaFolder = new File(tmpDir, schema);

        if (!mediaFolder.exists() || !mediaFolder.isDirectory()) {
            return 0;
        }

        return Arrays.stream(Objects.requireNonNull(mediaFolder.listFiles()))
                .filter(file -> DIGITAL_MEDIA_FILE_PATTERN.matcher(file.getName()).find())
                .count();
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

    private static void writeLine(String newLine) throws RestoreException {
        UpdatesDAO updatesDAO = UpdatesDAO.getInstance();

        try (Connection connection = updatesDAO.beginUpdate()) {
            Statement statement = connection.createStatement();

            statement.execute(newLine);

            connection.commit();
        } catch (SQLException e) {
            throw new RestoreException(e);
        }
    }

    private record ObservableIterator<T>(Iterator<T> iterator, Consumer<T> inspector)
            implements Iterator<T> {

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            T next = iterator.next();

            inspector.accept(next);

            return next;
        }
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
}

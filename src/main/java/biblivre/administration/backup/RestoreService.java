/*
 * ******************************************************************************
 *  * Este arquivo é parte do Biblivre5.
 *  *
 *  * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 *  * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *  * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *  * Licença, ou (caso queira) qualquer versão posterior.
 *  *
 *  * Este programa é distribuído na esperança de que possa ser  útil,
 *  * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  * Licença Pública Geral GNU para maiores detalhes.
 *  *
 *  * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *  *
 *  * @author Cleydyr de Albuquerque <cleydyr@biblivre.cloud>
 *  *****************************************************************************
 */

package biblivre.administration.backup;

import biblivre.administration.backup.exception.RestoreException;
import biblivre.administration.setup.State;
import biblivre.core.utils.CharPool;
import biblivre.database.util.PostgreSQLStatementIterable;
import biblivre.digitalmedia.DigitalMediaDAO;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RestoreService {
    private DataSource datasource;

    private DigitalMediaDAO digitalMediaDAO;

    public void processRestore(File restore) throws RestoreException {

        if (restore == null) {
            log.info("===== Skipping File 'null' =====");
            return;
        }

        if (!restore.exists()) {
            log.info(
                    "===== Skipping File '{}' =====",
                    restore.getName()); // NOPMD - suppressed GuardLogStatement - TODO explain
            // reason for suppression
            return;
        }

        log.info("===== Restoring File '{}' =====", restore.getName());

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

    public static boolean isFunctionOrTriggerRelated(String statement) {
        return Arrays.stream(FILTERED_OUT_STATEMENT_PREFIXES).anyMatch(statement::startsWith);
    }

    private void executePostgresCopyCommand(String copyCommand, Iterator<Character> sourceIterator)
            throws RestoreException {
        sourceIterator.next(); // Skip the first character (new line) of the COPY command

        try (Connection connection = datasource.getConnection()) {
            connection.setAutoCommit(false);

            PGConnection pgConnection = connection.unwrap(PGConnection.class);

            CopyManager copyManager = pgConnection.getCopyAPI();

            copyManager.copyIn(copyCommand, new PostgresCopyDataReader(sourceIterator));
        } catch (SQLException | IOException e) {
            throw new RestoreException(e);
        }
    }

    private void setPGSearchPath(String schema) throws RestoreException {
        writeLine(
                        """
                        SET search_path = "%s", pg_catalog
                        """
                        .formatted(schema));
    }

    private void writeLine(String newLine) throws RestoreException {
        try (Connection connection = datasource.getConnection()) {
            connection.setAutoCommit(false);

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

    public void removeNonExistingPGSchemaFromSchemasTable() throws RestoreException {
        writeLine(
                """
                        DELETE FROM "global".schemas
                        WHERE "schema" not in
                            (SELECT schema_name FROM information_schema.schemata)
                        """);
    }

    public static long getStepsFromMediaFolder(File tmpDir, String schema) {
        File mediaFolder = new File(tmpDir, schema);

        if (!mediaFolder.exists() || !mediaFolder.isDirectory()) {
            return 0;
        }

        return Arrays.stream(Objects.requireNonNull(mediaFolder.listFiles()))
                .filter(file -> DIGITAL_MEDIA_FILE_PATTERN.matcher(file.getName()).find())
                .count();
    }

    public void deleteSchemaFromSchemasTable(String originalSchemaName) throws RestoreException {
        writeLine(
                        """
                        DELETE FROM "global".schemas WHERE "schema" = '%s'
                        """
                        .formatted(originalSchemaName));
    }

    public void addSchemaToSchemaTable(String targetSchemaName, String escapedSchemaTitle)
            throws RestoreException {
        writeLine(buildInsertSchemaQuery(targetSchemaName, escapedSchemaTitle));
    }

    private static String buildInsertSchemaQuery(String finalSchemaName, String schemaTitle) {
        return
                """
                INSERT INTO "global".schemas (schema, name) VALUES ('%s', E'%s');
                """
                .formatted(finalSchemaName, schemaTitle);
    }

    public void dropSchema(String schemaToBeDeleted) throws RestoreException {
        writeLine(
                        """
                        DROP SCHEMA "%s" CASCADE
                        """
                        .formatted(schemaToBeDeleted));
    }

    public void deleteAllDigitalMedia(String schemaToBeDeleted) throws RestoreException {
        writeLine(
                        """
                        DELETE FROM "%s".digital_media
                        """
                        .formatted(schemaToBeDeleted));
    }

    private String buildUpdateDigitalMediaQuery(String mediaId, long oid) {
        return "UPDATE digital_media SET blob = '%d' WHERE id = '%s';".formatted(oid, mediaId);
    }

    private void processMediaRestoreFolder(File path) throws RestoreException {
        if (path == null) {
            log.info("===== Skipping File 'null' =====");
            return;
        }

        if (!path.exists() || !path.isDirectory()) {
            log.info("===== Skipping File '{}' =====", path.getName());
            return;
        }

        for (File file : Objects.requireNonNull(path.listFiles())) {
            Matcher fileMatcher = DIGITAL_MEDIA_FILE_PATTERN.matcher(file.getName());

            if (!fileMatcher.find()) {
                continue;
            }

            log.info("===== Restoring File '{}' =====", file.getName());

            String mediaId = fileMatcher.group(1);

            long oid = digitalMediaDAO.importFile(file);

            State.incrementCurrentStep();

            String newLine = buildUpdateDigitalMediaQuery(mediaId, oid);

            writeLine(newLine);
        }
    }

    public void processSchemaRestores(File path, String extension, String schema)
            throws RestoreException {

        State.writeLog("Processing schema for '" + schema + "'");

        processRestore(new File(path, schema + ".schema." + extension));

        State.writeLog("Processing data for '" + schema + "'");

        processRestore(new File(path, schema + ".data." + extension));

        State.writeLog("Processing media for '" + schema + "'");

        processMediaRestore(new File(path, schema + ".media." + extension), schema);

        processMediaRestoreFolder(new File(path, schema));
    }

    public void processSchemaRenames(Map<String, String> preRenameSchemas) throws RestoreException {
        for (Map.Entry<String, String> entry : preRenameSchemas.entrySet()) {
            String originalSchemaName = entry.getKey();

            String finalSchemaName = entry.getValue();

            State.writeLog(
                    "Renaming schema %s to %s".formatted(originalSchemaName, finalSchemaName));

            changePGSchemaName(originalSchemaName, finalSchemaName);
        }
    }

    private void changePGSchemaName(String originalSchemaName, String finalSchemaName)
            throws RestoreException {
        writeLine(
                        """
                        ALTER SCHEMA "%s" RENAME TO "%s"
                        """
                        .formatted(originalSchemaName, finalSchemaName));
    }

    private void processsOpenOid(String line, Map<Long, Long> oidMap) throws RestoreException {
        Matcher loOpenMatcher = LO_OPEN_COMMAND_PATTERN.matcher(line);

        if (!loOpenMatcher.find()) {
            return;
        }

        Long oid = Long.valueOf(loOpenMatcher.group(2));

        String newLine = loOpenMatcher.replaceFirst("$1" + oidMap.get(oid) + "$3");

        writeLine(newLine);
    }

    private void processMediaRestore(File restore, String schema) throws RestoreException {

        if (restore == null) {
            log.info("===== Skipping File 'null' =====");
            return;
        }

        if (!restore.exists()) {
            log.info("===== Skipping File '{}' =====", restore.getName());
            return;
        }

        log.info("===== Restoring File '{}' =====", restore.getName());

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
                String query =
                        buildUpdateDigitalMediaQuery(
                                String.valueOf(entry.getKey()), entry.getValue());

                writeLine(query);
            }
        } catch (IOException e) {
            throw new RestoreException(e);
        }
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

                log.info(line);

                return;
            }

            writeLine(line);
        }
    }

    private void processNewOid(String line, Map<Long, Long> oidMap) {
        Matcher loCreateMatcher = LO_CREATE_COMMAND_PATTERN.matcher(line);

        if (loCreateMatcher.find()) {
            Long currentOid = Long.valueOf(loCreateMatcher.group(1));

            Long newOid = digitalMediaDAO.createOID();

            log.info("Creating new OID (old: {}, new: {})", currentOid, newOid);

            oidMap.put(currentOid, newOid);
        }
    }

    private static boolean ignoreLine(String line) {
        return line.startsWith("ALTER LARGE OBJECT")
                || line.startsWith("BEGIN;")
                || line.startsWith("COMMIT;");
    }

    private static final String[] FILTERED_OUT_STATEMENT_PREFIXES =
            new String[] {"CREATE FUNCTION", "ALTER FUNCTION", "CREATE TRIGGER"};

    private static final Pattern LO_OPEN_COMMAND_PATTERN =
            Pattern.compile("(.*lo_open\\(')(.*?)(',.*)");

    private static final Pattern DIGITAL_MEDIA_FILE_PATTERN = Pattern.compile("^(\\d+)_(.*)$");

    private static final Pattern LO_CREATE_COMMAND_PATTERN =
            Pattern.compile("lo_create\\('(.*?)'\\)");

    @Autowired
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }
}

package biblivre.update.v6_0_0$9_0_7$alpha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.AbstractContainerDatabaseTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PSQLException;

class UpdateTest extends AbstractContainerDatabaseTest {

    private static final String PROBE_SCHEMA = "hibernate_report_upgrade";
    private static final String OTHER_OWNER_SCHEMA = "hibernate_report_other_owner";
    private static final String OTHER_OWNER = "report_table_owner";

    @Test
    void attachesSerialDefaultToHibernateStyleReportId() throws Exception {
        try (Connection connection = openConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + PROBE_SCHEMA + " CASCADE");
            statement.execute("CREATE SCHEMA " + PROBE_SCHEMA);
            // Column order matches Hibernate 6 ddl-auto=update for Report.
            statement.execute(
                    """
                    CREATE TABLE hibernate_report_upgrade.report (
                        id BIGINT NOT NULL,
                        description VARCHAR(255),
                        digital_media_id BIGINT NOT NULL,
                        name VARCHAR(255),
                        schema VARCHAR(255),
                        PRIMARY KEY (id)
                    )
                    """);

            try {
                insertSampleReport(statement);
                fail("Insert without id should fail on a Hibernate-created report table");
            } catch (PSQLException exception) {
                assertTrue(
                        exception
                                .getMessage()
                                .contains("null value in column \"id\" of relation \"report\""));
                assertTrue(
                        exception
                                .getMessage()
                                .contains("Failing row contains (null, 01, 2, teste, single)."));
            }

            Update.ensureIdSerialDefault(connection, PROBE_SCHEMA, "report");

            insertSampleReport(statement);

            try (ResultSet resultSet =
                    statement.executeQuery(
                            "SELECT id, name, description, schema, digital_media_id"
                                    + " FROM hibernate_report_upgrade.report")) {
                assertTrue(resultSet.next());
                assertEquals(1L, resultSet.getLong("id"));
                assertEquals("teste", resultSet.getString("name"));
                assertEquals("01", resultSet.getString("description"));
                assertEquals("single", resultSet.getString("schema"));
                assertEquals(2L, resultSet.getLong("digital_media_id"));
            }

            Update.ensureIdSerialDefault(connection, PROBE_SCHEMA, "report");
        } finally {
            try (Connection connection = openConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA IF EXISTS " + PROBE_SCHEMA + " CASCADE");
            }
        }
    }

    @Test
    void attachesSerialDefaultWhenReportTableHasAnotherOwner() throws Exception {
        try (Connection connection = openConnection();
                Statement statement = connection.createStatement()) {
            resetOtherOwnerFixture(statement);
            statement.execute("CREATE ROLE " + OTHER_OWNER + " NOLOGIN");
            statement.execute("CREATE SCHEMA " + OTHER_OWNER_SCHEMA);
            statement.execute(
                    """
                    CREATE TABLE hibernate_report_other_owner.report (
                        id BIGINT NOT NULL,
                        description VARCHAR(255),
                        digital_media_id BIGINT NOT NULL,
                        name VARCHAR(255),
                        schema VARCHAR(255),
                        PRIMARY KEY (id)
                    )
                    """);
            statement.execute(
                    "ALTER TABLE " + OTHER_OWNER_SCHEMA + ".report OWNER TO " + OTHER_OWNER);
            statement.execute("CREATE SEQUENCE " + OTHER_OWNER_SCHEMA + ".report_id_seq");

            assertNotEquals(
                    OTHER_OWNER, relationOwner(statement, OTHER_OWNER_SCHEMA, "report_id_seq"));

            Update.ensureIdSerialDefault(connection, OTHER_OWNER_SCHEMA, "report");

            assertEquals(OTHER_OWNER, relationOwner(statement, OTHER_OWNER_SCHEMA, "report"));
            assertEquals(
                    OTHER_OWNER, relationOwner(statement, OTHER_OWNER_SCHEMA, "report_id_seq"));

            statement.executeUpdate(
                    """
                    INSERT INTO hibernate_report_other_owner.report
                        (name, description, schema, digital_media_id)
                    VALUES ('teste', '01', 'single', 2)
                    """);

            try (ResultSet resultSet =
                    statement.executeQuery("SELECT id FROM hibernate_report_other_owner.report")) {
                assertTrue(resultSet.next());
                assertEquals(1L, resultSet.getLong("id"));
                assertFalse(resultSet.next());
            }

            Update.ensureIdSerialDefault(connection, OTHER_OWNER_SCHEMA, "report");
        } finally {
            try (Connection connection = openConnection();
                    Statement statement = connection.createStatement()) {
                resetOtherOwnerFixture(statement);
            }
        }
    }

    private static void resetOtherOwnerFixture(Statement statement) throws SQLException {
        statement.execute("DROP SCHEMA IF EXISTS " + OTHER_OWNER_SCHEMA + " CASCADE");
        statement.execute("DROP ROLE IF EXISTS " + OTHER_OWNER);
    }

    private static String relationOwner(Statement statement, String schema, String relation)
            throws SQLException {
        try (ResultSet resultSet =
                statement.executeQuery(
                        """
                        SELECT pg_get_userbyid(c.relowner)
                        FROM pg_class c
                        JOIN pg_namespace n ON n.oid = c.relnamespace
                        WHERE n.nspname = '%s' AND c.relname = '%s'
                        """
                                .formatted(schema, relation))) {
            assertTrue(resultSet.next(), schema + "." + relation);
            return resultSet.getString(1);
        }
    }

    private static void insertSampleReport(Statement statement) throws SQLException {
        statement.executeUpdate(
                """
                INSERT INTO hibernate_report_upgrade.report
                    (name, description, schema, digital_media_id)
                VALUES ('teste', '01', 'single', 2)
                """);
    }

    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
    }
}

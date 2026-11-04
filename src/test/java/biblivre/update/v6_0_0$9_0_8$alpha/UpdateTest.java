package biblivre.update.v6_0_0$9_0_8$alpha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UpdateTest extends AbstractContainerDatabaseTest {

    // Schema names Postgres must quote (uppercase, leading digit) come back quoted from
    // pg_get_serial_sequence. A missing language falls back to pt-BR.
    @ParameterizedTest
    @CsvSource({
        "orphan_user_types, , Tipo de usuário ausente (#15), pt-BR",
        "Orphan_User_Types, en-US, Missing user type (#15), en-US",
        "2orphan_user_types, es, Tipo de usuario ausente (#15), es",
    })
    void restoresPlaceholderTypesAndRejectsNewOrphans(
            String schema, String configuredLanguage, String expectedName, String textLanguage)
            throws Exception {
        String quotedSchema = "\"" + schema + "\"";

        try (Connection connection = openConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + quotedSchema + " CASCADE");
            statement.execute("CREATE SCHEMA " + quotedSchema);
            statement.execute("SET search_path TO " + quotedSchema);
            statement.execute(
                    """
                    CREATE TABLE configurations (
                        key varchar NOT NULL,
                        value varchar NOT NULL
                    )
                    """);
            if (configuredLanguage != null) {
                statement.execute(
                        "INSERT INTO configurations (key, value)"
                                + " VALUES ('general.default_language', '%s')"
                                        .formatted(configuredLanguage));
            }
            statement.execute(
                    """
                    CREATE TABLE users_types (
                        id integer PRIMARY KEY,
                        name varchar NOT NULL,
                        description varchar,
                        lending_limit integer,
                        reservation_limit integer,
                        lending_time_limit integer,
                        reservation_time_limit integer,
                        fine_value real DEFAULT 0 NOT NULL,
                        created_by integer
                    )
                    """);
            statement.execute("CREATE SEQUENCE users_types_id_seq OWNED BY users_types.id");
            statement.execute(
                    "ALTER TABLE users_types ALTER COLUMN id"
                            + " SET DEFAULT nextval('users_types_id_seq')");
            statement.execute("SELECT setval('users_types_id_seq', 1, false)");
            statement.execute(
                    """
                    INSERT INTO users_types (id, name, fine_value)
                    VALUES (1, 'Leitor', 0.5)
                    """);
            statement.execute(
                    """
                    CREATE TABLE users (
                        id integer PRIMARY KEY,
                        name varchar NOT NULL,
                        type integer,
                        status varchar NOT NULL
                    )
                    """);
            statement.execute(
                    """
                    INSERT INTO users (id, name, type, status) VALUES
                        (10, 'Leitor ativo sem tipo', 15, 'active'),
                        (11, 'Leitor inativo sem tipo', 15, 'inactive'),
                        (12, 'Leitor com tipo existente', 1, 'active')
                    """);

            Update.restoreMissingUserTypes(connection);

            try (ResultSet resultSet =
                    statement.executeQuery(
                            """
                            SELECT name, description, lending_limit, reservation_limit,
                                   lending_time_limit, reservation_time_limit, fine_value
                            FROM users_types
                            WHERE id = 15
                            """)) {
                assertTrue(resultSet.next());
                assertEquals(expectedName, resultSet.getString("name"));
                assertEquals(
                        Update.PLACEHOLDER_TEXTS.get(textLanguage).description(),
                        resultSet.getString("description"));
                assertEquals(1, resultSet.getInt("lending_limit"));
                assertEquals(1, resultSet.getInt("reservation_limit"));
                assertEquals(7, resultSet.getInt("lending_time_limit"));
                assertEquals(7, resultSet.getInt("reservation_time_limit"));
                assertEquals(0f, resultSet.getFloat("fine_value"));
                assertFalse(resultSet.next());
            }

            try (ResultSet resultSet =
                    statement.executeQuery(
                            "SELECT name, fine_value FROM users_types WHERE id = 1")) {
                assertTrue(resultSet.next());
                assertEquals("Leitor", resultSet.getString("name"));
                assertEquals(0.5f, resultSet.getFloat("fine_value"));
            }

            try (ResultSet resultSet =
                    statement.executeQuery("SELECT nextval('users_types_id_seq')")) {
                assertTrue(resultSet.next());
                assertEquals(16L, resultSet.getLong(1));
            }

            try (ResultSet resultSet =
                    statement.executeQuery(
                            """
                            SELECT 1 FROM pg_indexes
                            WHERE schemaname = current_schema()
                              AND indexname = 'FKI_users_users_types'
                            """)) {
                assertTrue(resultSet.next());
            }

            SQLException insertOrphan =
                    assertThrows(
                            SQLException.class,
                            () ->
                                    statement.execute(
                                            """
                                            INSERT INTO users (id, name, type, status)
                                            VALUES (13, 'Novo órfão', 99, 'active')
                                            """));
            assertTrue(insertOrphan.getMessage().contains("FK_users_users_types"));

            SQLException deletePlaceholder =
                    assertThrows(
                            SQLException.class,
                            () -> statement.execute("DELETE FROM users_types WHERE id = 15"));
            assertTrue(deletePlaceholder.getMessage().contains("FK_users_users_types"));

            Update.restoreMissingUserTypes(connection);

            try (ResultSet resultSet =
                    statement.executeQuery("SELECT count(*) FROM users_types WHERE id = 15")) {
                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getInt(1));
            }

            // A rerun must not move the sequence backwards.
            try (ResultSet resultSet =
                    statement.executeQuery("SELECT nextval('users_types_id_seq')")) {
                assertTrue(resultSet.next());
                assertTrue(resultSet.getLong(1) > 16L);
            }
        } finally {
            try (Connection connection = openConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA IF EXISTS " + quotedSchema + " CASCADE");
            }
        }
    }

    private static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword());
    }
}

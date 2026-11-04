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
import org.junit.jupiter.api.Test;

class UpdateTest extends AbstractContainerDatabaseTest {

    private static final String SCHEMA = "orphan_user_types";

    @Test
    void restoresPlaceholderTypesAndRejectsNewOrphans() throws Exception {
        try (Connection connection = openConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
            statement.execute("CREATE SCHEMA " + SCHEMA);
            statement.execute("SET search_path TO " + SCHEMA);
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
                assertEquals(Update.placeholderName(15), resultSet.getString("name"));
                assertEquals(Update.PLACEHOLDER_DESCRIPTION, resultSet.getString("description"));
                assertEquals(0, resultSet.getInt("lending_limit"));
                assertEquals(0, resultSet.getInt("reservation_limit"));
                assertEquals(0, resultSet.getInt("lending_time_limit"));
                assertEquals(0, resultSet.getInt("reservation_time_limit"));
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
        } finally {
            try (Connection connection = openConnection();
                    Statement statement = connection.createStatement()) {
                statement.execute("DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
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

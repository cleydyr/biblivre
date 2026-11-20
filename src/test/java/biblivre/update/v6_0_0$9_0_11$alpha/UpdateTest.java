package biblivre.update.v6_0_0$9_0_11$alpha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

class UpdateTest extends AbstractContainerDatabaseTest {

    @Test
    void backfillsStorageKeyFromBlobAndAllowsMediaWithoutLargeObject() throws Exception {
        try (Connection connection = openConnection();
                Statement statement = connection.createStatement()) {
            statement.execute("DROP SCHEMA IF EXISTS storage_key_update CASCADE");
            statement.execute("CREATE SCHEMA storage_key_update");
            statement.execute("SET search_path TO storage_key_update");
            statement.execute(
                    """
                    CREATE TABLE digital_media (
                        id integer NOT NULL,
                        name character varying,
                        blob oid NOT NULL,
                        content_type character varying,
                        size bigint,
                        created timestamp without time zone DEFAULT now() NOT NULL,
                        created_by integer
                    )
                    """);
            statement.execute(
                    "INSERT INTO digital_media (id, name, blob, content_type, size)"
                            + " VALUES (1, 'old.pdf', 4242, 'application/pdf', 3)");

            new Update().doUpdateScopedBySchema(connection);
            // Running twice must be harmless: restore re-applies the same DDL.
            new Update().doUpdateScopedBySchema(connection);

            try (ResultSet rs =
                    statement.executeQuery("SELECT storage_key FROM digital_media WHERE id = 1")) {
                assertTrue(rs.next());
                assertEquals("4242", rs.getString(1));
            }

            statement.execute(
                    "INSERT INTO digital_media (id, name, blob, storage_key, content_type, size)"
                            + " VALUES (2, 'new.pdf', NULL, 'single/abc', 'application/pdf', 3)");

            statement.execute(
                    """
                    INSERT INTO digital_media_pending_upload
                        (id, storage_key, name, content_type, size, expires_at)
                    VALUES (gen_random_uuid(), 'single/def', 'a.pdf', 'application/pdf', 3, now())
                    """);

            try (ResultSet rs =
                    statement.executeQuery("SELECT count(*) FROM digital_media_pending_upload")) {
                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
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

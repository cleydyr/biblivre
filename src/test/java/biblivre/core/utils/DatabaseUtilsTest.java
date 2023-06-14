package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.SchemaThreadLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class DatabaseUtilsTest extends AbstractContainerDatabaseTest {

    private static final String DEFAULT_SCHEMA = "single";

    @BeforeEach
    void setDefaultSchema() {
        SchemaThreadLocal.setSchema(DEFAULT_SCHEMA);
    }

    @Test
    void testGetDatabaseHostName() {
        execute(() -> assertEquals("localhost", DatabaseUtils.getDatabaseHostName()));
    }

    @Test
    void testGetDatabaseName() {
        execute(() -> assertEquals(container.getDatabaseName(), DatabaseUtils.getDatabaseName()));
    }

    @Test
    void testGetDatabasePort() {
        execute(
                () ->
                        assertEquals(
                                container.getMappedPort(Constants.DEFAULT_POSTGRESQL_PORT),
                                Integer.valueOf(DatabaseUtils.getDatabasePort())));
    }

    @Test
    void testGetDatabasePassword() {
        execute(() -> assertEquals(container.getPassword(), DatabaseUtils.getDatabasePassword()));
    }

    @Test
    void testGetDatabaseUsername() {
        execute(() -> assertEquals(container.getUsername(), DatabaseUtils.getDatabaseUsername()));
    }
}

package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
@Disabled
class DatabaseUtilsTest extends AbstractContainerDatabaseTest {

    private static final String DEFAULT_SCHEMA = "single";

    @BeforeEach
    void setDefaultSchema() {
        SchemaThreadLocal.setSchema(DEFAULT_SCHEMA);
    }

    @Test
    void testGetDatabaseHostName() {
        assertEquals("localhost", DatabaseUtils.getDatabaseHostName());
    }

    @Test
    void testGetDatabaseName() {
        assertEquals(postgreSQLContainer.getDatabaseName(), DatabaseUtils.getDatabaseName());
    }

    @Test
    void testGetDatabasePort() {
        assertEquals(
                postgreSQLContainer.getMappedPort(Constants.DEFAULT_POSTGRESQL_PORT),
                Integer.valueOf(DatabaseUtils.getDatabasePort()));
    }

    @Test
    void testGetDatabasePassword() {
        assertEquals(postgreSQLContainer.getPassword(), DatabaseUtils.getDatabasePassword());
    }

    @Test
    void testGetDatabaseUsername() {
        assertEquals(postgreSQLContainer.getUsername(), DatabaseUtils.getDatabaseUsername());
    }
}

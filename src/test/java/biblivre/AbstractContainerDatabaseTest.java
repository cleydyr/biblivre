package biblivre;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

public abstract class AbstractContainerDatabaseTest {
    private static DataSource datasource;

    public static final PostgreSQLContainer<?> postgreSQLContainer =
            SharedPostgreSQLContainer.getInstance();

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        postgreSQLContainer.stop();
    }
}

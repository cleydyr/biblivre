package biblivre;

import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;

public abstract class AbstractContainerDatabaseTest {
    private static DataSource datasource;

    public static final PostgreSQLContainer<?> postgreSQLContainer =
            SharedPostgreSQLContainer.getInstance();

    @BeforeAll
    static void beforeAll() {
        postgreSQLContainer.start();
    }
}

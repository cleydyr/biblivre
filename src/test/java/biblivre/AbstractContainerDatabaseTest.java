package biblivre;

import javax.sql.DataSource;
import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerDatabaseTest {
    private static DataSource datasource;

    @ClassRule
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            SharedPostgreSQLContainer.getInstance();

    public AbstractContainerDatabaseTest() {
        postgreSQLContainer.start();
    }
}

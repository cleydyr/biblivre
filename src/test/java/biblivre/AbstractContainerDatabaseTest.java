package biblivre;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.core.AbstractDAO;
import biblivre.core.utils.Constants;
import com.github.stefanbirkner.systemlambda.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.ClassRule;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerDatabaseTest {
    private static DataSource dataSource;

    @ClassRule
    protected final PostgreSQLContainer<?> postgreSQLContainer =
            SharedPostgreSQLContainer.getInstance();

    public AbstractContainerDatabaseTest() {
        postgreSQLContainer.start();
    }

    protected <T extends AbstractDAO> T getInstance(Class<T> clazz) {
        return AbstractDAO.getInstance(__ -> getDataSource(), clazz, "single");
    }

    protected DataSource getDataSource() {

        if (dataSource == null) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
            hikariConfig.setUsername(postgreSQLContainer.getUsername());
            hikariConfig.setPassword(postgreSQLContainer.getPassword());
            hikariConfig.setDriverClassName(postgreSQLContainer.getDriverClassName());

            dataSource = new HikariDataSource(hikariConfig);
        }

        return dataSource;
    }

    protected void execute(Statement statement) {
        try {
            withEnvironmentVariable(
                            Constants.DATABASE_PORT,
                            String.valueOf(
                                    postgreSQLContainer.getMappedPort(
                                            Constants.DEFAULT_POSTGRESQL_PORT)))
                    .and(Constants.DATABASE_NAME, postgreSQLContainer.getDatabaseName())
                    .and(Constants.DATABASE_PASSWORD, postgreSQLContainer.getPassword())
                    .and(Constants.DATABASE_USERNAME, postgreSQLContainer.getUsername())
                    .execute(statement);
        } catch (Exception e) {
            fail(e);
        }
    }
}

package biblivre;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.fail;

import biblivre.core.AbstractDAO;
import biblivre.core.utils.Constants;
import com.github.stefanbirkner.systemlambda.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

public abstract class AbstractContainerDatabaseTest {
    private static DataSource dataSource;

    protected static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:12")
                    .withDatabaseName(Constants.DEFAULT_DATABASE_NAME)
                    .withUsername(Constants.DEFAULT_DATABASE_USERNAME)
                    .withPassword(Constants.DEFAULT_DATABASE_PASSWORD)
                    .withCopyToContainer(
                            MountableFile.forClasspathResource("sql/biblivre4.sql"),
                            "/docker-entrypoint-initdb.d/populate-initial-data.sql");

    private static boolean setup = false;

    private static final Logger logger =
            LoggerFactory.getLogger(AbstractContainerDatabaseTest.class);

    @BeforeAll
    static void setUp() {
        if (!setup) {
            try {
                logger.info("Starting container");

                container.start();

                logger.info("Setup complete!");
            } catch (Exception e) {
                e.printStackTrace();
            }

            setup = true;
        }
    }

    @AfterAll
    static void close() {
        container.close();
    }

    protected static <T extends AbstractDAO> T getInstance(Class<T> clazz) {
        return AbstractDAO.getInstance(__ -> getDataSource(container), clazz, "single");
    }

    protected static DataSource getDataSource(JdbcDatabaseContainer<?> container) {

        if (dataSource == null) {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(container.getJdbcUrl());
            hikariConfig.setUsername(container.getUsername());
            hikariConfig.setPassword(container.getPassword());
            hikariConfig.setDriverClassName(container.getDriverClassName());

            dataSource = new HikariDataSource(hikariConfig);
        }

        return dataSource;
    }

    protected void execute(Statement statement) {
        try {
            withEnvironmentVariable(
                            Constants.DATABASE_PORT,
                            String.valueOf(
                                    container.getMappedPort(Constants.DEFAULT_POSTGRESQL_PORT)))
                    .and(Constants.DATABASE_NAME, container.getDatabaseName())
                    .and(Constants.DATABASE_PASSWORD, container.getPassword())
                    .and(Constants.DATABASE_USERNAME, container.getUsername())
                    .execute(statement);
        } catch (Exception e) {
            fail(e);
        }
    }
}

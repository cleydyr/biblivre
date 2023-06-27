package biblivre;

import biblivre.core.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.MountableFile;

public class SharedPostgreSQLContainer extends PostgreSQLContainer<SharedPostgreSQLContainer> {
    private static final String IMAGE_VERSION = "postgres:12";
    private static SharedPostgreSQLContainer container;

    private SharedPostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static SharedPostgreSQLContainer getInstance() {
        if (container == null) {
            container = new SharedPostgreSQLContainer();
        }

        return container
                .withDatabaseName(Constants.DEFAULT_DATABASE_NAME)
                .withUsername(Constants.DEFAULT_DATABASE_USERNAME)
                .withPassword(Constants.DEFAULT_DATABASE_PASSWORD)
                .withCopyToContainer(
                        MountableFile.forClasspathResource("sql/biblivre4.sql"),
                        "/docker-entrypoint-initdb.d/populate-initial-data.sql");
    }

    @Override
    public void start() {
        super.start();

        container.waitUntilContainerStarted();
    }

    @Override
    public void stop() {}

    private static final Logger logger = LoggerFactory.getLogger(SharedPostgreSQLContainer.class);
}

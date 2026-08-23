package biblivre;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractContainerDatabaseTest {

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

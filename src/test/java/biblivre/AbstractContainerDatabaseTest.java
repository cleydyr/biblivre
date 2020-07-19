package biblivre;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import com.github.stefanbirkner.systemlambda.Statement;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import biblivre.core.AbstractDAO;
import biblivre.core.utils.Constants;

public abstract class AbstractContainerDatabaseTest {
	private static DataSource dataSource;

	protected static PostgreSQLContainer<?> container =
		new PostgreSQLContainer<>("postgres:11");

	private static boolean setup = false;

	private static final Logger logger =
		LoggerFactory.getLogger(AbstractContainerDatabaseTest.class);

	@BeforeAll
	static void setUp() {
		if (!setup) {
			try {
				logger.info("Starting container");

				container.start();

				logger.info("Creating and populating database with default "
					+ "data");

				String createDatabaseSQL =
					_readSQLAsString("sql/createdatabase.sql");

				performQuery(container, createDatabaseSQL);

				String populateDatabaseSQL =
					_readSQLAsString("sql/biblivre4.sql");

				performQuery(container, populateDatabaseSQL);

				logger.info("Setup complete!");
			} catch (Exception e) {
				e.printStackTrace();
			}

			setup = true;
		}
	}

	protected static <T extends AbstractDAO> T getInstance(Class<T> clazz) {
		return AbstractDAO.getInstance(
			__ -> getDataSource(container), clazz, "single");
	}

	protected static void performQuery(
		JdbcDatabaseContainer<?> container, String sql)
		throws SQLException {

		DataSource ds = getDataSource(container);
		java.sql.Statement statement = ds.getConnection().createStatement();
		statement.execute(sql);
	}

	protected static DataSource getDataSource(
		JdbcDatabaseContainer<?> container) {

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

	private static String _readSQLAsString(String path) throws Exception {
		ClassLoader classLoader =
			AbstractContainerDatabaseTest.class.getClassLoader();

		URL resource = classLoader.getResource(path);

		return new String(Files.readAllBytes(Paths.get(resource.toURI())));
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

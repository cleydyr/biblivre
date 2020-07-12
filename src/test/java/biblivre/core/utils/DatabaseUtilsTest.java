package biblivre.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;

@Testcontainers
class DatabaseUtilsTest extends AbstractContainerDatabaseTest {

	private static final String DEFAULT_SCHEMA = "single";

	@Test
	void testGetPgDump() {
		execute(() -> {
			String dummyPath = _getTempFilePath();

			_saveSingleConfiguration(Constants.CONFIG_PGDUMP_PATH, dummyPath);

			File pgDump = DatabaseUtils.getPgDump(DEFAULT_SCHEMA);

			assertNotNull(pgDump);

			assertEquals(dummyPath, pgDump.getAbsolutePath());

			_resetConfiguration(Constants.CONFIG_PGDUMP_PATH);
		});
	}


	@Test
	void testGetPsql() {
		execute(() -> {
			String path = _getTempFilePath();

			_saveSingleConfiguration(Constants.CONFIG_PSQL_PATH, path);

			File psql = DatabaseUtils.getPsql(DEFAULT_SCHEMA);

			assertNotNull(psql);

			assertEquals(path, psql.getAbsolutePath());

			_resetConfiguration(Constants.CONFIG_PSQL_PATH);
		});
	}

	@Test
	void testGetPgDumpFromConfiguration() {
		execute(() -> {
			assertNull(
				DatabaseUtils.getPgDumpFromConfiguration(DEFAULT_SCHEMA));

			String path = _getTempFilePath();

			_saveSingleConfiguration(Constants.CONFIG_PGDUMP_PATH, path);

			File pgDump =
				DatabaseUtils.getPgDumpFromConfiguration(DEFAULT_SCHEMA);

			assertNotNull(pgDump);

			assertEquals(path, pgDump.getAbsolutePath());

			_resetConfiguration(Constants.CONFIG_PGDUMP_PATH);
		});
	}

	@Test
	void testGetPsqlFromConfiguration() {
		execute(() -> {
			assertNull(
				DatabaseUtils.getPsqlFromConfiguration(DEFAULT_SCHEMA));

			String path = _getTempFilePath();

			_saveSingleConfiguration(Constants.CONFIG_PSQL_PATH, path);

			File pgDump =
				DatabaseUtils.getPsqlFromConfiguration(DEFAULT_SCHEMA);

			assertNotNull(pgDump);

			assertEquals(path, pgDump.getAbsolutePath());

			_resetConfiguration(Constants.CONFIG_PSQL_PATH);
		});
	}

	@Test
	void testGetDatabaseHostName() {
		execute(() -> {
			assertEquals("localhost", DatabaseUtils.getDatabaseHostName());
		});
	}

	@Test
	void testGetDatabaseName() {
		execute(() -> {
			assertEquals(
				container.getDatabaseName(), DatabaseUtils.getDatabaseName());
		});
	}

	@Test
	void testGetDatabasePort() {
		execute(() -> {
			assertEquals(
				container.getMappedPort(Constants.DEFAULT_POSTGRESQL_PORT),
				Integer.valueOf(DatabaseUtils.getDatabasePort()));
		});
	}

	@Test
	void testGetDatabasePassword() {
		execute(() -> {
			assertEquals(
				container.getPassword(), DatabaseUtils.getDatabasePassword());
		});
	}

	@Test
	void testGetDatabaseUsername() {
		execute(() -> {
			assertEquals(
				container.getUsername(), DatabaseUtils.getDatabaseUsername());
		});
	}

	private String _getTempFilePath() throws IOException {
		Path tempFile = Files.createTempFile("foo", "bar");

		String path = tempFile.toString();
		return path;
	}

	private void _resetConfiguration(String config) {
		ConfigurationsDTO nullConfiguration =
			new ConfigurationsDTO(config, null);

		Configurations.save(DEFAULT_SCHEMA, nullConfiguration, 1);
	}

	private void _saveSingleConfiguration(String config, String value) {
		ConfigurationsDTO configuration = new ConfigurationsDTO(config, value);

		Configurations.save(DEFAULT_SCHEMA, configuration, 1);
	}
}

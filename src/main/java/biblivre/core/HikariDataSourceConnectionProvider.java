package biblivre.core;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;

import biblivre.core.utils.DatabaseUtils;

public class HikariDataSourceConnectionProvider {
	private static com.zaxxer.hikari.HikariDataSource ds;
	private static final String SET_SCHEMA_TEMPLATE = "SET search_path = '%s', public, pg_catalog;";
	private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s";

	static {

		Properties props = new Properties();
		props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
		props.setProperty("dataSource.user", System.getenv("PGUSER"));
		props.setProperty("dataSource.password", DatabaseUtils.getDatabasePassword());
		props.setProperty("dataSource.url", String.format(JDBC_URL_TEMPLATE,
				DatabaseUtils.getDatabaseHostName(), DatabaseUtils.getDatabasePort(),
				DatabaseUtils.getDatabaseName()));

		props.setProperty("maximumPoolSize", "10");
		props.setProperty("minimumIdle", "5");

		props.put("dataSource.logWriter", new PrintWriter(System.out));

		ds = new com.zaxxer.hikari.HikariDataSource( new HikariConfig(props) );
	}

	private HikariDataSourceConnectionProvider() {}

	public static DataSource getDataSource() {
		return ds;
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

	public static Connection getConnection(String schema) throws SQLException {
		Connection connection = getConnection();

		if (schema != null) {
			try (Statement createStatement = connection.createStatement()) {
				createStatement.execute(String.format(SET_SCHEMA_TEMPLATE, schema));
			}
		}

		return connection;
	}
}
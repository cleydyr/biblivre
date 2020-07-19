package biblivre.core;

import java.io.PrintWriter;
import java.util.Properties;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import biblivre.core.utils.DatabaseUtils;

public class HikariDataSourceProvider implements DataSourceProvider {
	private static final String JDBC_URL_TEMPLATE =
		"jdbc:postgresql://%s:%s/%s";

	@Override
	public DataSource getDataSource(String databaseName) {
		Properties props = new Properties();

		props.setProperty(
			"dataSourceClassName", PGSimpleDataSource.class.getName());

		props.setProperty(
			"dataSource.user", DatabaseUtils.getDatabaseUsername());

		props.setProperty(
			"dataSource.password", DatabaseUtils.getDatabasePassword());

		props.setProperty(
			"dataSource.url",
			String.format(
				JDBC_URL_TEMPLATE, DatabaseUtils.getDatabaseHostName(),
				DatabaseUtils.getDatabasePort(), databaseName));

		props.setProperty("maximumPoolSize", "10");

		props.setProperty("minimumIdle", "5");

		props.put("dataSource.logWriter", new PrintWriter(System.out));

		return new HikariDataSource( new HikariConfig(props) );
	}
}

package biblivre.digitalmedia;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.testcontainers.containers.JdbcDatabaseContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class AbstractContainerDatabaseTest {
	private DataSource dataSource;

	protected void performQuery(JdbcDatabaseContainer<?> container, String sql) throws SQLException {
		DataSource ds = getDataSource(container);
		Statement statement = ds.getConnection().createStatement();
		statement.execute(sql);
	}

	protected DataSource getDataSource(JdbcDatabaseContainer<?> container) {
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
}
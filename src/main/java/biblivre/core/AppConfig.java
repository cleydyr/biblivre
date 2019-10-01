package biblivre.core;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import biblivre.administration.permissions.PermissionConfiguration;
import biblivre.core.utils.DatabaseUtils;

@Configuration
@Import(PermissionConfiguration.class)
@ComponentScan(basePackages = "biblivre.core")
public class AppConfig {
	private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%s/biblivre4";

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()
			.driverClassName("org.postgresql.ds.PGSimpleDataSource")
			.username(DatabaseUtils.getDatabaseUser())
			.password(DatabaseUtils.getDatabasePassword())
			.url(String.format(JDBC_URL_TEMPLATE,
				DatabaseUtils.getDatabaseHostName(), DatabaseUtils.getDatabasePort()))
			.build();
	}
}

package biblivre.core;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import biblivre.administration.permissions.PermissionConfiguration;
import biblivre.cataloging.CatalogingConfiguration;
import biblivre.core.utils.DatabaseUtils;
import biblivre.z3950.client.config.Z3950Configuration;

@Configuration
@Import({ PermissionConfiguration.class, CatalogingConfiguration.class, Z3950Configuration.class })
@ComponentScan(basePackages = "biblivre.core")
public class AppConfig {
	private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%s/biblivre4";

	@Bean
	public DataSource dataSource() {
		return DataSourceBuilder.create()
				.username(DatabaseUtils.getDatabaseUser())
				.password(DatabaseUtils.getDatabasePassword())
				.url(
						String.format(
								JDBC_URL_TEMPLATE, DatabaseUtils.getDatabaseHostName(),
								DatabaseUtils.getDatabasePort())
				)
				.build();
	}
}

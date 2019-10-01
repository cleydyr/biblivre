package biblivre.administration.permissions;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import biblivre.core.HandlerBeanNameGenerator;

@Configuration
@ComponentScan(
		basePackages = "biblivre.administration.permissions",
		nameGenerator = HandlerBeanNameGenerator.class
)
public class PermissionConfiguration {
	@Bean
	public PermissionDAOFactory permissionDAOFactory(DataSource dataSource) {
		return new PermissionDAOFactoryImpl(dataSource);
	}

	@Bean
	public PermissionBOFactory permissionBOFactory(DataSource dataSource) {
		return new PermissionBOFactoryImpl(permissionDAOFactory(dataSource));
	}
}

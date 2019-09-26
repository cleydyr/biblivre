package biblivre.administration.permissions;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "biblivre.administration.permissions")
public class PermissionConfiguration {
	@Bean
	public PermissionDAOFactory permissionDAOFactory() {
		return new PermissionDAOFactoryImpl();
	}

	@Bean
	public PermissionBOFactory permissionBOFactory() {
		return new PermissionBOFactoryImpl(permissionDAOFactory());
	}
}

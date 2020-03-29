package biblivre.administration.accesscontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.accesscontrol.AccessControlDAO;
import biblivre.circulation.accesscontrol.AccessControlPersistence;

@Configuration
public class AccessControlConfig {
	@Bean
	public AccessControlBO accessControlBO() {
		return new AccessControlBO(getAccessControlPersistence());
	}

	@Bean
	public AccessControlPersistence getAccessControlPersistence() {
		return AccessControlDAO.getInstance("global");
	}
}

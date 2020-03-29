package biblivre.administration.accesscontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.accesscontrol.AccessControlDAO;
import biblivre.circulation.accesscontrol.AccessControlPersistence;
import biblivre.circulation.accesscontrol.Handler;

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

	@Bean(name = "circulation.accesscontrol")
	public Handler handler() {
		return new Handler();
	}
}

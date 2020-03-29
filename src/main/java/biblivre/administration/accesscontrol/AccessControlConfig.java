package biblivre.administration.accesscontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.accesscontrol.AccessControlDAO;
import biblivre.circulation.accesscontrol.AccessControlPersistence;
import biblivre.circulation.accesscontrol.Handler;
import biblivre.circulation.user.Validator;

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

	@Bean(name = "circulation.user")
	public Validator validator() {
		return new Validator(accessControlBO());
	}
}

package biblivre.administration.accesscontrol;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDAO;
import biblivre.administration.accesscards.AccessCardPersistence;
import biblivre.circulation.accesscontrol.AccessControlBO;
import biblivre.circulation.accesscontrol.AccessControlDAO;
import biblivre.circulation.accesscontrol.AccessControlPersistence;
import biblivre.circulation.accesscontrol.Handler;
import biblivre.circulation.user.Validator;

@Configuration
public class AccessControlConfig {
	@Bean
	public AccessControlBO accessControlBO() {
		return new AccessControlBO(getAccessControlPersistence(), accessCardBO());
	}

	@Bean
	public AccessControlPersistence getAccessControlPersistence() {
		return AccessControlDAO.getInstance("global");
	}

	@Bean
	public Handler handler() {
		return new Handler(accessControlBO(), accessCardBO());
	}

	@Bean
	public biblivre.administration.accesscards.Handler anotherHandler() {
		return new biblivre.administration.accesscards.Handler(accessCardBO());
	}

	@Bean(name = "circulation.user")
	public Validator validator() {
		return new Validator(accessControlBO());
	}

	@Bean
	public AccessCardBO accessCardBO() {
		return new AccessCardBO(accessCardPersistence());
	}

	@Bean
	public AccessCardPersistence accessCardPersistence() {
		return AccessCardDAO.getInstance("global");
	}
}

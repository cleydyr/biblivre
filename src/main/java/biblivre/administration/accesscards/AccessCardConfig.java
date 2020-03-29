package biblivre.administration.accesscards;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessCardConfig {
	@Bean
	public Handler anotherHandler() {
		return new Handler(accessCardBO());
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

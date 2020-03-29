package biblivre.circulation.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import biblivre.administration.accesscontrol.AccessControlConfig;

@Configuration
@Import(AccessControlConfig.class)
public class UserConfig {
	@Autowired
	private AccessControlConfig _accessControlConfig;

	@Bean(name = "circulation.user")
	public Validator validator() {
		return new Validator(_accessControlConfig.accessControlBO());
	}
}

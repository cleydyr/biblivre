package biblivre;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import biblivre.administration.accesscards.AccessCardConfig;
import biblivre.circulation.accesscontrol.AccessControlConfig;
import biblivre.circulation.user.UserConfig;

@Configuration
@Import({AccessCardConfig.class, UserConfig.class, AccessControlConfig.class})
public class BiblivreApp {
}

package biblivre;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import biblivre.administration.accesscards.AccessCardConfig;
import biblivre.administration.accesscontrol.AccessControlConfig;
import biblivre.circulation.user.UserConfig;

@Configuration
@Import({AccessCardConfig.class, UserConfig.class, AccessControlConfig.class})
public class BiblivreConfig {

}

package biblivre.login.audit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@ConditionalOnProperty(name = "login-audit.enabled", havingValue = "true")
public class LoginAuditRestClientConfig {

    @Bean
    @ConditionalOnMissingBean
    RestClient.Builder loginAuditRestClientBuilder() {
        return RestClient.builder();
    }
}

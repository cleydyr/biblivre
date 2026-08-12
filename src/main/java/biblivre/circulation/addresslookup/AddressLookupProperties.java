package biblivre.circulation.addresslookup;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "biblivre.circulation.address-lookup")
public class AddressLookupProperties {
    private boolean enabled = true;
    private String baseUrl = "https://viacep.com.br";
    private Duration connectTimeout = Duration.ofSeconds(2);
    private Duration readTimeout = Duration.ofSeconds(5);
}

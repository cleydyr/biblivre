package biblivre.cataloging.isbnlookup;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "biblivre.cataloging.isbn-lookup")
public class IsbnLookupProperties {
    /** Value of {@link #provider} that turns the lookup off. */
    public static final String DISABLED_PROVIDER = "none";

    /** {@code none} (disabled), {@code google-books} or {@code open-library}. */
    private String provider = DISABLED_PROVIDER;

    /** Optional credential sent to the provider (Google Books: the {@code key} parameter). */
    private String apiKey = "";

    /** Overrides the provider's default base URL when set (proxies, tests). */
    private String baseUrl = "";

    private Duration connectTimeout = Duration.ofSeconds(2);

    private Duration readTimeout = Duration.ofSeconds(5);

    public boolean isEnabled() {
        return provider != null
                && !provider.isBlank()
                && !DISABLED_PROVIDER.equalsIgnoreCase(provider.trim());
    }

    public boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    String baseUrlOr(String defaultBaseUrl) {
        return baseUrl == null || baseUrl.isBlank() ? defaultBaseUrl : baseUrl;
    }
}

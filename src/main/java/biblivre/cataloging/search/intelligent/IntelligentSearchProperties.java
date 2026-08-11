package biblivre.cataloging.search.intelligent;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "biblivre.search.intelligent")
public class IntelligentSearchProperties {
    private boolean enabled = false;
    private int candidateLimit = 40;
    private int rrfK = 50;
    private Embedding embedding = new Embedding();

    @Getter
    @Setter
    public static class Embedding {
        /** openai_compatible | hashing | local */
        private String provider = "openai_compatible";

        private String baseUrl = "https://api.openai.com/v1";
        private String apiKey = "";
        private String model = "bge-m3";
        private int dimensions = 1024;
        private int batchSize = 64;

        /** TCP connect timeout for openai_compatible HTTP calls. */
        private Duration connectTimeout = Duration.ofSeconds(2);

        /** Response read timeout for openai_compatible HTTP calls. */
        private Duration readTimeout = Duration.ofSeconds(30);
    }
}

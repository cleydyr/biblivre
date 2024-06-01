package biblivre.search;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;

@Configuration
public class ElasticsearchClientConfiguration extends ElasticsearchConfiguration {
    @Value("${elasticsearch.server.url}")
    String serverUrl;

    @Value("${elasticsearch.api.key}")
    String apiKey;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(serverUrl)
                .usingSsl()
                .withHeaders(
                        () -> {
                            var headers = new HttpHeaders();
                            headers.add("Authorization", STR."ApiKey \{apiKey}");

                            return headers;
                        })
                .build();
    }
}

package biblivre.cataloging.isbnlookup;

import java.net.http.HttpClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/** Builds HTTP clients for providers, honoring the configured base URL and timeouts. */
final class IsbnLookupRestClients {

    private IsbnLookupRestClients() {}

    static RestClient create(
            RestClient.Builder builder, IsbnLookupProperties properties, String defaultBaseUrl) {
        HttpClient httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(properties.getConnectTimeout())
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());

        return builder.clone()
                .baseUrl(properties.baseUrlOr(defaultBaseUrl))
                .requestFactory(requestFactory)
                .build();
    }
}

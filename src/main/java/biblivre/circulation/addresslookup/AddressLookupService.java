package biblivre.circulation.addresslookup;

import biblivre.circulation.addresslookup.AddressLookupException.Reason;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.net.http.HttpClient;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class AddressLookupService {
    private final AddressLookupProperties properties;
    private final RestClient restClient;

    public AddressLookupService(
            AddressLookupProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;

        var httpClient =
                HttpClient.newBuilder().connectTimeout(properties.getConnectTimeout()).build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());

        this.restClient =
                restClientBuilder
                        .baseUrl(properties.getBaseUrl())
                        .requestFactory(requestFactory)
                        .build();
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public AddressLookupResult lookup(String rawCep) {
        if (!properties.isEnabled()) {
            throw new AddressLookupException(Reason.DISABLED, "Address lookup is disabled");
        }

        String cep = normalizeCep(rawCep);
        if (!isValidBrazilianCep(cep)) {
            throw new AddressLookupException(Reason.INVALID_CEP, "Invalid CEP");
        }

        try {
            ViaCepResponse response =
                    restClient
                            .get()
                            .uri("/ws/{cep}/json/", cep)
                            .retrieve()
                            .body(ViaCepResponse.class);

            if (response == null) {
                throw new AddressLookupException(Reason.UPSTREAM_ERROR, "Empty upstream response");
            }

            if (Boolean.TRUE.equals(response.erro())) {
                throw new AddressLookupException(Reason.NOT_FOUND, "CEP not found");
            }

            String street = textOrEmpty(response.logradouro());
            String neighborhood = textOrEmpty(response.bairro());
            String city = textOrEmpty(response.localidade());
            String state = textOrEmpty(response.uf());

            if (city.isBlank() && state.isBlank() && street.isBlank() && neighborhood.isBlank()) {
                throw new AddressLookupException(Reason.NOT_FOUND, "CEP not found");
            }

            return new AddressLookupResult(street, neighborhood, city, state, street.isBlank());
        } catch (AddressLookupException e) {
            throw e;
        } catch (RestClientException e) {
            throw new AddressLookupException(Reason.UPSTREAM_ERROR, "Upstream request failed", e);
        } catch (Exception e) {
            throw new AddressLookupException(Reason.UPSTREAM_ERROR, "Failed to parse response", e);
        }
    }

    static String normalizeCep(String rawCep) {
        if (rawCep == null) {
            return "";
        }
        return rawCep.replaceAll("\\D", "");
    }

    static boolean isValidBrazilianCep(String normalizedCep) {
        return normalizedCep != null && normalizedCep.matches("\\d{8}");
    }

    private static String textOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ViaCepResponse(
            String logradouro, String bairro, String localidade, String uf, Boolean erro) {}
}

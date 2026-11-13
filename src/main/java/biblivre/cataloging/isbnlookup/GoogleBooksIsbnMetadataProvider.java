package biblivre.cataloging.isbnlookup;

import biblivre.cataloging.isbnlookup.IsbnLookupException.Reason;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.marc4j.marc.Record;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Google Books API (the catalog behind Google Play Books).
 *
 * <p>Works without a key on a small shared quota that is often exhausted; set {@code
 * biblivre.cataloging.isbn-lookup.api-key} to a Google Cloud API key with the Books API enabled.
 */
@Component
public class GoogleBooksIsbnMetadataProvider implements IsbnMetadataProvider {
    public static final String NAME = "google-books";

    static final String DEFAULT_BASE_URL = "https://www.googleapis.com";

    private static final String VOLUMES_PATH = "/books/v1/volumes?q=isbn:{isbn}&maxResults=1";

    private final IsbnLookupProperties properties;
    private final RestClient restClient;

    public GoogleBooksIsbnMetadataProvider(
            IsbnLookupProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient =
                IsbnLookupRestClients.create(restClientBuilder, properties, DEFAULT_BASE_URL);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Optional<Record> lookup(String isbn) {
        RestClient.RequestHeadersSpec<?> request =
                properties.hasApiKey()
                        ? restClient
                                .get()
                                .uri(VOLUMES_PATH + "&key={key}", isbn, properties.getApiKey())
                        : restClient.get().uri(VOLUMES_PATH, isbn);

        VolumesResponse response;

        try {
            response = request.retrieve().body(VolumesResponse.class);
        } catch (RestClientException e) {
            // Spring puts the request URL, key included, in I/O error messages; drop the cause.
            throw new IsbnLookupException(
                    Reason.UPSTREAM_ERROR,
                    "Google Books request failed: "
                            + e.getClass().getSimpleName()
                            + ": "
                            + redactApiKey(e.getMessage()));
        }

        if (response == null || response.items() == null || response.items().isEmpty()) {
            return Optional.empty();
        }

        VolumeInfo info = response.items().get(0).volumeInfo();

        if (info == null || isBlank(info.title())) {
            return Optional.empty();
        }

        return Optional.of(BookMetadataMarcMapper.toRecord(toBookMetadata(isbn, info)));
    }

    private static BookMetadata toBookMetadata(String queriedIsbn, VolumeInfo info) {
        List<String> authors = info.authors() == null ? List.of() : info.authors();

        return new BookMetadata(
                isbn13(info).orElse(queriedIsbn),
                info.title(),
                info.subtitle(),
                String.join(", ", authors),
                authors,
                "",
                "",
                info.publisher(),
                BookMetadataMarcMapper.extractYear(info.publishedDate()),
                info.pageCount(),
                BookMetadataMarcMapper.toMarcLanguage(info.language()),
                info.description(),
                info.categories());
    }

    private String redactApiKey(String message) {
        if (message == null || !properties.hasApiKey()) {
            return message;
        }

        String key = properties.getApiKey();

        return message.replace(key, "***")
                .replace(URLEncoder.encode(key, StandardCharsets.UTF_8), "***");
    }

    private static Optional<String> isbn13(VolumeInfo info) {
        if (info.industryIdentifiers() == null) {
            return Optional.empty();
        }

        return info.industryIdentifiers().stream()
                .filter(identifier -> "ISBN_13".equals(identifier.type()))
                .map(IndustryIdentifier::identifier)
                .filter(identifier -> !isBlank(identifier))
                .findFirst();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record VolumesResponse(Integer totalItems, List<Volume> items) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Volume(VolumeInfo volumeInfo) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record VolumeInfo(
            String title,
            String subtitle,
            List<String> authors,
            String publisher,
            String publishedDate,
            String description,
            List<IndustryIdentifier> industryIdentifiers,
            Integer pageCount,
            List<String> categories,
            String language) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record IndustryIdentifier(String type, String identifier) {}
}

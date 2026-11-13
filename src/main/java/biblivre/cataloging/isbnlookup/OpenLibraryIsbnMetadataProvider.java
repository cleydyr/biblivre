package biblivre.cataloging.isbnlookup;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.marc4j.marc.Record;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Open Library Books API ({@code jscmd=data}). Needs no key. */
@Component
public class OpenLibraryIsbnMetadataProvider implements IsbnMetadataProvider {
    public static final String NAME = "open-library";

    static final String DEFAULT_BASE_URL = "https://openlibrary.org";

    private static final ParameterizedTypeReference<Map<String, OpenLibraryBook>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient restClient;

    public OpenLibraryIsbnMetadataProvider(
            IsbnLookupProperties properties, RestClient.Builder restClientBuilder) {
        this.restClient =
                IsbnLookupRestClients.create(restClientBuilder, properties, DEFAULT_BASE_URL);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Optional<Record> lookup(String isbn) {
        Map<String, OpenLibraryBook> response =
                restClient
                        .get()
                        .uri("/api/books?bibkeys=ISBN:{isbn}&format=json&jscmd=data", isbn)
                        .retrieve()
                        .body(RESPONSE_TYPE);

        if (response == null) {
            return Optional.empty();
        }

        OpenLibraryBook book = response.get("ISBN:" + isbn);

        if (book == null || book.title() == null || book.title().isBlank()) {
            return Optional.empty();
        }

        return Optional.of(BookMetadataMarcMapper.toRecord(toBookMetadata(isbn, book)));
    }

    private static BookMetadata toBookMetadata(String queriedIsbn, OpenLibraryBook book) {
        return new BookMetadata(
                isbn13(book).orElse(queriedIsbn),
                book.title(),
                book.subtitle(),
                book.byStatement(),
                names(book.authors()),
                "",
                first(names(book.publishPlaces())),
                first(names(book.publishers())),
                BookMetadataMarcMapper.extractYear(book.publishDate()),
                book.numberOfPages(),
                "",
                "",
                names(book.subjects()));
    }

    private static Optional<String> isbn13(OpenLibraryBook book) {
        if (book.identifiers() == null || book.identifiers().isbn13() == null) {
            return Optional.empty();
        }

        return book.identifiers().isbn13().stream()
                .filter(Objects::nonNull)
                .filter(isbn -> !isbn.isBlank())
                .findFirst();
    }

    private static List<String> names(List<Named> values) {
        if (values == null) {
            return List.of();
        }

        return values.stream().filter(Objects::nonNull).map(Named::name).toList();
    }

    private static String first(List<String> values) {
        return values.isEmpty() ? "" : values.get(0);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OpenLibraryBook(
            String title,
            String subtitle,
            @JsonProperty("by_statement") String byStatement,
            List<Named> authors,
            @JsonProperty("number_of_pages") Integer numberOfPages,
            Identifiers identifiers,
            List<Named> publishers,
            @JsonProperty("publish_places") List<Named> publishPlaces,
            @JsonProperty("publish_date") String publishDate,
            List<Named> subjects) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Identifiers(@JsonProperty("isbn_13") List<String> isbn13) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Named(String name) {}
}

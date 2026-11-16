package biblivre.cataloging.isbnlookup;

import static biblivre.cataloging.isbnlookup.MarcAssertions.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.Record;
import org.springframework.web.client.RestClient;

class GoogleBooksIsbnMetadataProviderTest {

    private static final String VOLUME =
            """
            {
              "kind": "books#volumes",
              "totalItems": 1,
              "items": [
                {
                  "kind": "books#volume",
                  "id": "abc123",
                  "volumeInfo": {
                    "title": "1984",
                    "subtitle": "Edição especial",
                    "authors": ["George Orwell", "Heloisa Jahn"],
                    "publisher": "Companhia das Letras",
                    "publishedDate": "2009-07-21",
                    "description": "Distopia clássica sobre vigilância.",
                    "industryIdentifiers": [
                      {"type": "ISBN_10", "identifier": "8535914846"},
                      {"type": "ISBN_13", "identifier": "9788535914849"}
                    ],
                    "pageCount": 416,
                    "categories": ["Fiction"],
                    "language": "pt-BR",
                    "imageLinks": {"thumbnail": "http://books.google.com/x"}
                  }
                }
              ]
            }
            """;

    private StubHttpServer server;

    @BeforeEach
    void startServer() throws IOException {
        server = new StubHttpServer();
    }

    @AfterEach
    void stopServer() {
        server.close();
    }

    private GoogleBooksIsbnMetadataProvider provider(IsbnLookupProperties properties) {
        return new GoogleBooksIsbnMetadataProvider(properties, RestClient.builder());
    }

    @Test
    void lookup_mapsVolumeInfoToMarc() {
        server.respond(200, VOLUME);

        Optional<Record> result = provider(server.properties()).lookup("8535914846");

        assertTrue(result.isPresent());
        Record record = result.get();
        assertEquals(List.of("9788535914849"), values(record, "020", 'a'));
        assertEquals(List.of("por"), values(record, "041", 'a'));
        assertEquals(List.of("George Orwell"), values(record, "100", 'a'));
        assertEquals(List.of("1984"), values(record, "245", 'a'));
        assertEquals(List.of("Edição especial"), values(record, "245", 'b'));
        assertEquals(List.of("George Orwell, Heloisa Jahn"), values(record, "245", 'c'));
        assertEquals(List.of("Companhia das Letras"), values(record, "260", 'b'));
        assertEquals(List.of("2009"), values(record, "260", 'c'));
        assertEquals(List.of("416 p."), values(record, "300", 'a'));
        assertEquals(List.of("Distopia clássica sobre vigilância."), values(record, "520", 'a'));
        assertEquals(List.of("Fiction"), values(record, "650", 'a'));
        assertEquals(List.of("Heloisa Jahn"), values(record, "700", 'a'));
    }

    @Test
    void lookup_queriesByIsbnWithoutKeyWhenNoneConfigured() {
        server.respond(200, VOLUME);

        provider(server.properties()).lookup("9788535914849");

        assertEquals("/books/v1/volumes", server.lastRequestUri().getPath());
        String query = server.lastRequestUri().getQuery();
        assertTrue(query.contains("q=isbn:9788535914849"), query);
        assertFalse(query.contains("key="), query);
    }

    @Test
    void lookup_sendsConfiguredApiKey() {
        server.respond(200, VOLUME);
        IsbnLookupProperties properties = server.properties();
        properties.setApiKey("secret+key/1");

        provider(properties).lookup("9788535914849");

        String rawQuery = server.lastRequestUri().getRawQuery();
        assertTrue(rawQuery.contains("key=secret%2Bkey%2F1"), rawQuery);
    }

    @Test
    void lookup_whenNoItems_returnsEmpty() {
        server.respond(200, "{\"kind\": \"books#volumes\", \"totalItems\": 0}");

        assertTrue(provider(server.properties()).lookup("9788535914849").isEmpty());
    }

    @Test
    void lookup_whenQuotaExceeded_throwsUpstreamError() {
        server.respond(429, "{\"error\": {\"code\": 429, \"status\": \"RESOURCE_EXHAUSTED\"}}");

        IsbnLookupException exception =
                assertThrows(
                        IsbnLookupException.class,
                        () -> provider(server.properties()).lookup("9788535914849"));

        assertEquals(IsbnLookupException.Reason.UPSTREAM_ERROR, exception.getReason());
    }

    @Test
    void lookup_whenUnreachable_keepsApiKeyOutOfTheError() {
        IsbnLookupProperties properties = server.properties();
        properties.setApiKey("secret+key/1");
        server.close();

        IsbnLookupException exception =
                assertThrows(
                        IsbnLookupException.class,
                        () -> provider(properties).lookup("9788535914849"));

        String message = exception.getMessage();
        assertFalse(message.contains("secret"), message);
        assertEquals(null, exception.getCause());
    }

    @Test
    void lookup_fallsBackToQueriedIsbnWithoutIsbn13() {
        server.respond(
                200,
                """
                {"totalItems": 1, "items": [{"volumeInfo": {"title": "Sem identificadores"}}]}
                """);

        Record record = provider(server.properties()).lookup("0140328726").orElseThrow();

        assertEquals(List.of("0140328726"), values(record, "020", 'a'));
        assertEquals(List.of("Sem identificadores"), values(record, "245", 'a'));
    }
}

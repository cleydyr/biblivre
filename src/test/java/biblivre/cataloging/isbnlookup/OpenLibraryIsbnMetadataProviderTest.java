package biblivre.cataloging.isbnlookup;

import static biblivre.cataloging.isbnlookup.MarcAssertions.tags;
import static biblivre.cataloging.isbnlookup.MarcAssertions.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.Record;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

class OpenLibraryIsbnMetadataProviderTest {

    // Trimmed from a real response for this ISBN (November 2026).
    private static final String BOOK =
            """
            {"ISBN:9788535914849": {
              "url": "http://openlibrary.org/books/OL26449397M/1984",
              "key": "/books/OL26449397M",
              "title": "1984",
              "authors": [{"url": "http://openlibrary.org/authors/OL15318546A/George_Orwell", "name": "George Orwell"}],
              "number_of_pages": 416,
              "pagination": "414p.",
              "identifiers": {"wikidata": ["Q111263683"], "isbn_13": ["9788535914849"], "openlibrary": ["OL26449397M"]},
              "classifications": {"dewey_decimal_class": ["823"]},
              "publishers": [{"name": "Companhia das Letras"}],
              "publish_places": [{"name": "São Paulo, SP, Brasil"}],
              "publish_date": "2009",
              "subjects": [
                {"name": "futurology", "url": "https://openlibrary.org/subjects/futurology"},
                {"name": "censorship", "url": "https://openlibrary.org/subjects/censorship"}
              ]
            }}
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

    private OpenLibraryIsbnMetadataProvider provider() {
        return new OpenLibraryIsbnMetadataProvider(server.properties(), RestClient.builder());
    }

    @Test
    void lookup_mapsBookToMarc() {
        server.respond(200, BOOK);

        Record record = provider().lookup("9788535914849").orElseThrow();

        assertEquals(List.of("020", "100", "245", "260", "300", "650", "650"), tags(record));
        assertEquals(List.of("9788535914849"), values(record, "020", 'a'));
        assertEquals(List.of("George Orwell"), values(record, "100", 'a'));
        assertEquals(List.of("1984"), values(record, "245", 'a'));
        assertEquals(List.of("São Paulo, SP, Brasil"), values(record, "260", 'a'));
        assertEquals(List.of("Companhia das Letras"), values(record, "260", 'b'));
        assertEquals(List.of("2009"), values(record, "260", 'c'));
        assertEquals(List.of("416 p."), values(record, "300", 'a'));
        assertEquals(List.of("futurology", "censorship"), values(record, "650", 'a'));
    }

    @Test
    void lookup_requestsBooksApiWithoutKey() {
        server.respond(200, BOOK);
        IsbnLookupProperties properties = server.properties();
        properties.setApiKey("ignored");

        new OpenLibraryIsbnMetadataProvider(properties, RestClient.builder())
                .lookup("9788535914849");

        assertEquals("/api/books", server.lastRequestUri().getPath());
        String query = server.lastRequestUri().getQuery();
        assertTrue(query.contains("bibkeys=ISBN:9788535914849"), query);
        assertTrue(query.contains("jscmd=data"), query);
        assertFalse(query.contains("ignored"), query);
    }

    @Test
    void lookup_usesByStatementAsResponsibility() {
        server.respond(
                200,
                """
                {"ISBN:0140328726": {
                  "title": "Fantastic Mr. Fox",
                  "subtitle": "a story",
                  "by_statement": "Roald Dahl ; illustrated by Quentin Blake.",
                  "publish_date": "October 1, 1988"
                }}
                """);

        Record record = provider().lookup("0140328726").orElseThrow();

        assertEquals(List.of("0140328726"), values(record, "020", 'a'));
        assertEquals(List.of("a story"), values(record, "245", 'b'));
        assertEquals(
                List.of("Roald Dahl ; illustrated by Quentin Blake."), values(record, "245", 'c'));
        assertEquals(List.of("1988"), values(record, "260", 'c'));
    }

    @Test
    void lookup_whenUnknownIsbn_returnsEmpty() {
        server.respond(200, "{}");

        assertTrue(provider().lookup("9788535914849").isEmpty());
    }

    @Test
    void lookup_whenServerFails_throws() {
        server.respond(500, "{}");

        assertThrows(RestClientException.class, () -> provider().lookup("9788535914849"));
    }
}

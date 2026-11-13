package biblivre.cataloging.isbnlookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.cataloging.isbnlookup.IsbnLookupException.Reason;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

class IsbnLookupServiceTest {

    private static final class FakeProvider implements IsbnMetadataProvider {
        private final String name;
        private final Function<String, Optional<Record>> answer;
        private final AtomicReference<String> lastIsbn = new AtomicReference<>();

        FakeProvider(String name, Function<String, Optional<Record>> answer) {
            this.name = name;
            this.answer = answer;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Optional<Record> lookup(String isbn) {
            lastIsbn.set(isbn);
            return answer.apply(isbn);
        }
    }

    private static IsbnLookupProperties properties(String provider) {
        IsbnLookupProperties properties = new IsbnLookupProperties();
        properties.setProvider(provider);
        return properties;
    }

    @Test
    void disabledByDefault() {
        IsbnLookupService service =
                new IsbnLookupService(
                        new IsbnLookupProperties(),
                        List.of(new FakeProvider("open-library", isbn -> Optional.empty())));

        assertFalse(service.isEnabled());

        IsbnLookupException exception =
                assertThrows(IsbnLookupException.class, () -> service.lookup("9788535914849"));
        assertEquals(Reason.DISABLED, exception.getReason());
    }

    @Test
    void unknownProvider_failsAtStartupListingAvailableOnes() {
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                new IsbnLookupService(
                                        properties("amazon"),
                                        List.of(
                                                new FakeProvider(
                                                        "open-library", isbn -> Optional.empty()),
                                                new FakeProvider(
                                                        "google-books",
                                                        isbn -> Optional.empty()))));

        assertTrue(exception.getMessage().contains("amazon"));
        assertTrue(exception.getMessage().contains("[google-books, open-library]"));
    }

    @Test
    void lookup_normalizesIsbnAndDelegatesToConfiguredProvider() {
        Record record = MarcFactory.newInstance().newRecord();
        FakeProvider other = new FakeProvider("open-library", isbn -> Optional.empty());
        FakeProvider configured = new FakeProvider("google-books", isbn -> Optional.of(record));

        IsbnLookupService service =
                new IsbnLookupService(properties(" Google-Books "), List.of(other, configured));

        IsbnLookupResult result = service.lookup("978-85-359-1484-9");

        assertTrue(service.isEnabled());
        assertEquals("9788535914849", configured.lastIsbn.get());
        assertEquals(null, other.lastIsbn.get());
        assertEquals("9788535914849", result.isbn());
        assertEquals("google-books", result.provider());
        assertSame(record, result.record());
    }

    @Test
    void lookup_rejectsInvalidIsbnWithoutCallingProvider() {
        FakeProvider provider = new FakeProvider("open-library", isbn -> Optional.empty());
        IsbnLookupService service =
                new IsbnLookupService(properties("open-library"), List.of(provider));

        IsbnLookupException exception =
                assertThrows(IsbnLookupException.class, () -> service.lookup("978-85-359-1484-8"));

        assertEquals(Reason.INVALID_ISBN, exception.getReason());
        assertEquals(null, provider.lastIsbn.get());
    }

    @Test
    void lookup_whenProviderHasNoRecord_throwsNotFound() {
        IsbnLookupService service =
                new IsbnLookupService(
                        properties("open-library"),
                        List.of(new FakeProvider("open-library", isbn -> Optional.empty())));

        IsbnLookupException exception =
                assertThrows(IsbnLookupException.class, () -> service.lookup("9788535914849"));

        assertEquals(Reason.NOT_FOUND, exception.getReason());
    }

    @Test
    void lookup_whenProviderFails_throwsUpstreamError() {
        IsbnLookupService service =
                new IsbnLookupService(
                        properties("open-library"),
                        List.of(
                                new FakeProvider(
                                        "open-library",
                                        isbn -> {
                                            throw new IllegalStateException("boom");
                                        })));

        IsbnLookupException exception =
                assertThrows(IsbnLookupException.class, () -> service.lookup("9788535914849"));

        assertEquals(Reason.UPSTREAM_ERROR, exception.getReason());
    }
}

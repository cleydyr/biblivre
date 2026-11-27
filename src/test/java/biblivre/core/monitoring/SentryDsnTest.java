package biblivre.core.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SentryDsnTest {

    @Test
    void parse_buildsEnvelopeUriFromHostAndProject() {
        SentryDsn dsn = SentryDsn.parse("https://abc123@errors.example.org/7");

        assertEquals("abc123", dsn.publicKey());
        assertEquals("https://errors.example.org/api/7/envelope/", dsn.envelopeUri().toString());
    }

    @Test
    void parse_keepsPortAndPathPrefix() {
        SentryDsn dsn = SentryDsn.parse("http://key@localhost:8000/glitchtip/3/");

        assertEquals(
                "http://localhost:8000/glitchtip/api/3/envelope/", dsn.envelopeUri().toString());
    }

    @Test
    void parse_rejectsDsnWithoutKey() {
        assertThrows(
                IllegalArgumentException.class, () -> SentryDsn.parse("https://example.org/1"));
    }

    @Test
    void parse_rejectsDsnWithoutProject() {
        assertThrows(
                IllegalArgumentException.class, () -> SentryDsn.parse("https://key@example.org"));
    }

    @Test
    void matches_ignoresTrailingSlashAndBlanks() {
        SentryDsn dsn = SentryDsn.parse("https://key@example.org/1");

        assertTrue(dsn.matches(" https://key@example.org/1/ "));
        assertFalse(dsn.matches("https://key@example.org/2"));
        assertFalse(dsn.matches(null));
    }
}

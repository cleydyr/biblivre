package biblivre.core.monitoring;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SentryEnvelopeTest {

    static final String DSN = "https://key@example.org/1";

    static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void parse_readsHeaderAndNewlineDelimitedItems() {
        SentryEnvelope envelope =
                SentryEnvelope.parse(
                        bytes(
                                "{\"dsn\":\""
                                        + DSN
                                        + "\"}\n{\"type\":\"event\"}\n{\"message\":\"boom\"}\n"));

        assertEquals(DSN, envelope.dsn());
        assertEquals(1, envelope.items().size());
        assertEquals("event", envelope.items().get(0).type());
        assertArrayEquals(bytes("{\"message\":\"boom\"}"), envelope.items().get(0).payload());
    }

    @Test
    void parse_readsLengthPrefixedPayloadContainingNewlines() {
        String payload = "line1\nline2";
        String body =
                "{\"dsn\":\""
                        + DSN
                        + "\"}\n{\"type\":\"attachment\",\"length\":"
                        + payload.length()
                        + "}\n"
                        + payload
                        + "\n{\"type\":\"event\"}\n{}";

        SentryEnvelope envelope = SentryEnvelope.parse(bytes(body));

        assertEquals(2, envelope.items().size());
        assertArrayEquals(bytes(payload), envelope.items().get(0).payload());
        assertEquals("event", envelope.items().get(1).type());
    }

    @Test
    void withoutItemTypes_dropsReplaysAndRoundTrips() {
        String body =
                "{\"dsn\":\""
                        + DSN
                        + "\"}\n"
                        + "{\"type\":\"replay_event\"}\n{}\n"
                        + "{\"type\":\"replay_recording\",\"length\":3}\nabc\n"
                        + "{\"type\":\"event\"}\n{\"a\":1}\n";

        SentryEnvelope filtered =
                SentryEnvelope.parse(bytes(body))
                        .withoutItemTypes(ClientErrorTunnelController.DROPPED_ITEM_TYPES);

        assertEquals(
                "{\"dsn\":\"" + DSN + "\"}\n{\"type\":\"event\"}\n{\"a\":1}\n",
                new String(filtered.toBytes(), StandardCharsets.UTF_8));
    }

    @Test
    void withoutItemTypes_canLeaveAnEmptyEnvelope() {
        SentryEnvelope envelope =
                SentryEnvelope.parse(bytes("{}\n{\"type\":\"replay_event\"}\n{}\n"))
                        .withoutItemTypes(Set.of("replay_event"));

        assertTrue(envelope.isEmpty());
    }

    @Test
    void parse_rejectsMalformedHeader() {
        assertThrows(IllegalArgumentException.class, () -> SentryEnvelope.parse(bytes("nope\n")));
    }

    @Test
    void parse_rejectsLengthBeyondBody() {
        assertThrows(
                IllegalArgumentException.class,
                () -> SentryEnvelope.parse(bytes("{}\n{\"type\":\"event\",\"length\":99}\n{}")));
    }
}

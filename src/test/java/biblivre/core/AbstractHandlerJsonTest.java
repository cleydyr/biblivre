package biblivre.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Null {@link IFJson} values must take the {@code Object} overload. A bare {@code null} literal is
 * more specific as {@code IFJson} than as {@code Object}, so the null branch recurses until the
 * stack overflows — the failure seen on {@code circulation.lending.return_immediate} when {@code
 * next_reservation} is absent.
 */
class AbstractHandlerJsonTest {

    private final RecordingHandler handler = new RecordingHandler();

    @BeforeEach
    void setUp() {
        HandlerContextThreadLocal.setHandlerContext(new HandlerContext());
    }

    @AfterEach
    void tearDown() {
        HandlerContextThreadLocal.remove();
    }

    @Test
    void putOpt_nullIfJson_omitsTheKey() {
        handler.putOpt("next_reservation", (IFJson) null);

        assertFalse(handler.getJson().has("next_reservation"));
    }

    @Test
    void putOpt_ifJson_storesItsJsonObject() {
        handler.putOpt("next_reservation", () -> new JSONObject().put("id", 7));

        assertEquals(7, handler.getJson().getJSONObject("next_reservation").getInt("id"));
    }

    @Test
    void put_nullIfJson_removesTheKey() {
        handler.put("data", "present");

        handler.put("data", (IFJson) null);

        assertFalse(handler.getJson().has("data"));
    }

    @Test
    void append_nullIfJson_completes() {
        handler.append("items", (IFJson) null);

        assertEquals("[null]", handler.getJson().getJSONArray("items").toString());
    }

    @Test
    void accumulate_nullIfJson_removesTheKey() {
        handler.accumulate("items", (IFJson) null);

        assertFalse(handler.getJson().has("items"));
    }

    private static final class RecordingHandler extends AbstractHandler {}
}

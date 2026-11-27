package biblivre.core.monitoring;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Minimal reader/writer for the Sentry envelope format: a JSON header line followed by items, each
 * a JSON header line and a payload. A payload is either {@code length} bytes (and may then hold
 * newlines or binary data) or runs to the next newline.
 *
 * @see <a href="https://develop.sentry.dev/sdk/data-model/envelopes/">Envelopes</a>
 */
public final class SentryEnvelope {

    private static final byte NEWLINE = '\n';

    private final byte[] headerLine;
    private final JSONObject header;
    private final List<Item> items;

    private SentryEnvelope(byte[] headerLine, JSONObject header, List<Item> items) {
        this.headerLine = headerLine;
        this.header = header;
        this.items = items;
    }

    public record Item(byte[] headerLine, String type, byte[] payload) {}

    public static SentryEnvelope parse(byte[] body) {
        Cursor cursor = new Cursor(body);

        byte[] headerLine = cursor.readLine();
        JSONObject header = parseJson(headerLine, "envelope header");

        List<Item> items = new ArrayList<>();

        while (cursor.hasMore()) {
            byte[] itemHeaderLine = cursor.readLine();

            if (itemHeaderLine.length == 0) {
                continue;
            }

            JSONObject itemHeader = parseJson(itemHeaderLine, "item header");
            String type = itemHeader.optString("type", "");

            byte[] payload;

            if (itemHeader.has("length")) {
                int length = itemHeader.optInt("length", -1);
                payload = cursor.read(length);
                cursor.skipNewline();
            } else {
                payload = cursor.readLine();
            }

            items.add(new Item(itemHeaderLine, type, payload));
        }

        return new SentryEnvelope(headerLine, header, Collections.unmodifiableList(items));
    }

    public String dsn() {
        return header.optString("dsn", "");
    }

    public List<Item> items() {
        return items;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public SentryEnvelope withoutItemTypes(Set<String> types) {
        List<Item> kept = items.stream().filter(item -> !types.contains(item.type())).toList();

        return new SentryEnvelope(headerLine, header, kept);
    }

    public byte[] toBytes() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.writeBytes(headerLine);
        out.write(NEWLINE);

        for (Item item : items) {
            out.writeBytes(item.headerLine());
            out.write(NEWLINE);
            out.writeBytes(item.payload());
            out.write(NEWLINE);
        }

        return out.toByteArray();
    }

    private static JSONObject parseJson(byte[] line, String what) {
        try {
            return new JSONObject(new String(line, StandardCharsets.UTF_8));
        } catch (JSONException e) {
            throw new IllegalArgumentException("Malformed " + what, e);
        }
    }

    private static final class Cursor {
        private final byte[] data;
        private int position;

        Cursor(byte[] data) {
            this.data = data;
        }

        boolean hasMore() {
            return position < data.length;
        }

        byte[] readLine() {
            int end = position;

            while (end < data.length && data[end] != NEWLINE) {
                end++;
            }

            byte[] line = Arrays.copyOfRange(data, position, end);
            position = Math.min(end + 1, data.length);

            return line;
        }

        byte[] read(int length) {
            if (length < 0 || position + length > data.length) {
                throw new IllegalArgumentException("Item length exceeds envelope size");
            }

            byte[] chunk = Arrays.copyOfRange(data, position, position + length);
            position += length;

            return chunk;
        }

        void skipNewline() {
            if (position < data.length && data[position] == NEWLINE) {
                position++;
            }
        }
    }
}

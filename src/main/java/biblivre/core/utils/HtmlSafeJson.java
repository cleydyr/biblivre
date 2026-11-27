package biblivre.core.utils;

import org.json.JSONObject;

/** Renders JSON values that are safe to embed inside an inline HTML {@code <script>} block. */
public final class HtmlSafeJson {

    private HtmlSafeJson() {}

    public static String quote(String value) {
        return escape(JSONObject.quote(value));
    }

    public static String stringify(JSONObject value) {
        return escape(value.toString());
    }

    private static String escape(String json) {
        return json.replace("<", "\\u003C").replace(">", "\\u003E").replace("&", "\\u0026");
    }
}

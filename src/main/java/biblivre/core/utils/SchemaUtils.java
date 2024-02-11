package biblivre.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

public class SchemaUtils {
    private static final Pattern pattern = Pattern.compile("<(?<left>[^,]+),(?<right>[^>]+)>");

    public static Map<String, Pair<String, String>> buildSchemasMap(String schemasJson) {
        Map<String, Pair<String, String>> map = new HashMap<>();

        JSONObject json = new JSONObject(schemasJson);

        json.keys()
                .forEachRemaining(
                        key -> {
                            Object schemaTitleAndSubtitle = json.get(key);

                            Pair<String, String> pair = getPair(key, schemaTitleAndSubtitle);

                            map.put(key, pair);
                        });

        return map;
    }

    private static Pair<String, String> getPair(String key, Object schemaTitleAndSubtitle) {
        if (schemaTitleAndSubtitle instanceof JSONObject jsonObject) {
            return getPairFromJSONObject(jsonObject);
        } else if (schemaTitleAndSubtitle instanceof String value) {
            return getPairFromFormattedString(value);
        } else {
            throw new IllegalArgumentException(
                    "Invalid value for schema title and subtitle: " + schemaTitleAndSubtitle);
        }
    }

    private static Pair<String, String> getPairFromFormattedString(String value) {
        Matcher matcher = pattern.matcher(value);

        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                    "Invalid value for schema title and subtitle: " + value);
        }

        String left = matcher.group("left");

        String right = matcher.group("right");

        return Pair.of(left, right);
    }

    private static Pair<String, String> getPairFromJSONObject(JSONObject jsonObject) {
        String left = jsonObject.getString("left");

        String right = jsonObject.getString("right");

        return Pair.of(left, right);
    }
}

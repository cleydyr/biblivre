package biblivre.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

public class SchemaUtils {
	public static Map<String, Pair<String, String>> buildSchemasMap(
			String schemasJson) {

		Map<String, Pair<String, String>> map =
			new HashMap<String, Pair<String, String>>();

		JSONObject json = new JSONObject(schemasJson);

		json.keys().forEachRemaining(key -> {
			JSONObject jsonObject = json.getJSONObject(key);

			String left = jsonObject.getString("left");
			String right = jsonObject.getString("right");

			Pair<String, String> pair = Pair.of(left, right);

			map.put(key, pair);
		});

		return map;
	}
}

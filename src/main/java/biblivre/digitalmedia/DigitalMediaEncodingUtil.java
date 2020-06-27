package biblivre.digitalmedia;

import java.util.Base64;
import java.util.Base64.Encoder;

public class DigitalMediaEncodingUtil {
	public static String getEncodedId(int id, String name) {
		if (name == null) {
			throw new IllegalArgumentException("name can't be null");
		}

		StringBuilder composedName = new StringBuilder(3)
			.append(id)
			.append(':')
			.append(name);

		Encoder encoder = Base64.getEncoder();

		String encodedId =
			new String(encoder.encode(composedName.toString().getBytes()));

		return encodedId.replaceAll("\\\\", "_");
	}
}

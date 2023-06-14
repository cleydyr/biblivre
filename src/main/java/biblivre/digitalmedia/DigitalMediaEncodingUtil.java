package biblivre.digitalmedia;

import java.util.Base64;
import java.util.Base64.Encoder;

public class DigitalMediaEncodingUtil {
    public static String getEncodedId(int id, String name) {
        if (name == null) {
            throw new IllegalArgumentException("name can't be null");
        }

        String composedName = String.valueOf(id) + ':' + name;

        Encoder encoder = Base64.getEncoder();

        String encodedId = new String(encoder.encode(composedName.getBytes()));

        return encodedId.replaceAll("\\\\", "_");
    }
}

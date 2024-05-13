package biblivre.digitalmedia;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class DigitalMediaEncodingUtil {
    public static String getEncodedId(long id, String name) {
        if (name == null) {
            throw new IllegalArgumentException("name can't be null");
        }

        String composedName = String.valueOf(id) + ':' + name;

        Encoder encoder = Base64.getEncoder();

        String encodedId = new String(encoder.encode(composedName.getBytes()));

        return encodedId.replaceAll("\\\\", "_");
    }

    public static Optional<Pair<Integer, String>> decode(String id, Charset charset) {
        Base64.Decoder decoder = Base64.getDecoder();

        String decodedId = new String(decoder.decode(id), charset);

        String[] splitId = decodedId.split(":");

        if (splitId.length != 2
                || !StringUtils.isNumeric(splitId[0])
                || StringUtils.isBlank(splitId[1])) {
            return Optional.empty();
        }

        return Optional.of(Pair.of(Integer.parseInt(splitId[0]), splitId[1]));
    }
}

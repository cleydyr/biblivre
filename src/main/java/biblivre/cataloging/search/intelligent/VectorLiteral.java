package biblivre.cataloging.search.intelligent;

import java.util.Locale;
import java.util.StringJoiner;

final class VectorLiteral {
    private VectorLiteral() {}

    static String toLiteral(float[] embedding) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (float value : embedding) {
            joiner.add(Float.toString(value));
        }
        return joiner.toString();
    }

    static String contentHash(String searchText) {
        return Integer.toHexString(searchText.toLowerCase(Locale.ROOT).hashCode());
    }
}

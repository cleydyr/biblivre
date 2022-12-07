package biblivre.core.utils;

import biblivre.core.enums.ParagraphAlignment;
import java.util.function.Supplier;

public class ParagraphAlignmentUtil {
    public static int getHorizontalAlignmentConfigurationValue(
            Supplier<? extends Integer> defaultValue, String configurationValue) {

        int horizontalAlignment =
                ParagraphAlignment.valueOf(configurationValue)
                        .getAlignment()
                        .orElseGet(defaultValue);
        return horizontalAlignment;
    }
}

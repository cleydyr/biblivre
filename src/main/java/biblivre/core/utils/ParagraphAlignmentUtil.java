package biblivre.core.utils;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ParagraphAlignment;
import java.util.function.Supplier;

public class ParagraphAlignmentUtil {
    public static int getHorizontalAlignmentConfigurationValue(Supplier<? extends Integer> defaultValue) {

        String schema = SchemaThreadLocal.get();

		String configurationValue =
                Configurations.getString(schema , Constants.CONFIG_LABEL_PRINT_PARAGRAPH_ALIGNMENT);
        int horizontalAlignment =
                ParagraphAlignment.valueOf(configurationValue)
                        .getAlignment()
                        .orElseGet(defaultValue);
        return horizontalAlignment;
    }
}

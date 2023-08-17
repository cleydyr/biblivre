package biblivre.core.utils;

public class PropertiesUtil {
    public static String getConfigFromEnv(String key, String defaultValue) {
        String value = System.getenv(key);

        if (value != null) {
            return value;
        }

        return defaultValue;
    }
}

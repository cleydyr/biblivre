package biblivre.core;

public interface RequestParserHelper {
    boolean isMustRedirectToSchema(String path);

    String parseController(String path, String defaultValue);
}

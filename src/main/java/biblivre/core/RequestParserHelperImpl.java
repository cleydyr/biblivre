package biblivre.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class RequestParserHelperImpl implements RequestParserHelper {

    @Override
    public boolean isMustRedirectToSchema(String path) {
        if (StringUtils.isBlank(path)) {
            return false;
        }

        String[] urlArray = path.split("/");

        return urlArray.length <= 1 && !path.endsWith("/");
    }

    @Override
    public String parseController(String path, String defaultValue) {
        if (StringUtils.isNotBlank(path)) {
            String[] urlArray = path.split("/");

            String schema = urlArray[0];

            if ("DigitalMediaController".equals(schema)) {
                return "DigitalMediaController";
            }

            if (urlArray.length > 1) {
                return urlArray[1];
            }
        }

        return defaultValue;
    }
}

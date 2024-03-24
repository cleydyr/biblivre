package biblivre.core.controllers;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;

public class SchemaUtil {
    private static final Collection<String> RESERVED_PATHS =
            Arrays.asList(
                    "api", "DigitalMediaController", "static", "favicon.ico", "login", "logout");

    public static String extractSchema(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String requestURI = httpServletRequest.getRequestURI();

        String contextPath = httpServletRequest.getContextPath();

        String url = requestURI.substring(contextPath.length() + 1);

        String schema = getSchemaFromRequestHeaders(request);

        if (schema != null) {
            return schema;
        }

        if (!url.isEmpty()) {
            String[] urlArray = url.split("/");

            if (urlArray.length == 0 || isReservedPath(urlArray[0])) {
                return null;
            }

            return urlArray[0];
        }

        return null;
    }

    private static boolean isReservedPath(String s) {
        return RESERVED_PATHS.contains(s);
    }

    private static String getSchemaFromRequestHeaders(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        return httpServletRequest.getHeader("X-Biblivre-Schema");
    }
}

package biblivre.core.controllers;

import biblivre.core.utils.Constants;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

public class SchemaUtil {
    public static final String SPA_SEARCH_PATH = "/spa/search";
    public static final String SPA_SCHEMA_QUERY_PARAM = "schema";
    public static final String SPA_SHOW_SELECT_SCHEMA_QUERY_PARAM = "showSelectSchema";

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

    public static String buildSpaSearchHref(String contextPath, String schema) {
        String href = contextPath + SPA_SEARCH_PATH;

        if (Constants.GLOBAL_SCHEMA.equals(schema)) {
            return href + "?" + SPA_SHOW_SELECT_SCHEMA_QUERY_PARAM;
        }

        if (StringUtils.isNotBlank(schema)) {
            return href
                    + "?"
                    + SPA_SCHEMA_QUERY_PARAM
                    + "="
                    + URLEncoder.encode(schema, StandardCharsets.UTF_8);
        }

        return href;
    }
}

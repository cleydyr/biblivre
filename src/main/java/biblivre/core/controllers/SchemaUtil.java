package biblivre.core.controllers;

import biblivre.core.schemas.SchemaDTO;
import biblivre.core.utils.Constants;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SchemaUtil {
    public static final String SPA_SEARCH_PATH = "/spa/search";
    public static final String SPA_STATIC_PATH = "/static/spa";
    public static final String SPA_SCHEMA_QUERY_PARAM = "schema";
    public static final String SPA_SHOW_SELECT_SCHEMA_QUERY_PARAM = "showSelectSchema";
    public static final String BOUND_SCHEMA_HEADER = "X-Biblivre-Bound-Schema";

    private static final Collection<String> RESERVED_PATHS =
            Arrays.asList(
                    "api",
                    "DigitalMediaController",
                    "static",
                    "favicon.ico",
                    "login",
                    "logout",
                    "spa");

    public static String extractSchema(ServletRequest request) {
        String schema = getSchemaFromRequestHeaders(request);

        if (schema != null) {
            return schema;
        }

        return extractPathSchema(request);
    }

    public static String extractBoundSchema(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String boundSchema = httpServletRequest.getHeader(BOUND_SCHEMA_HEADER);
        return StringUtils.isBlank(boundSchema) ? null : boundSchema.trim();
    }

    public static String extractPathSchema(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String requestURI = httpServletRequest.getRequestURI();
        String contextPath = httpServletRequest.getContextPath();

        if (requestURI.length() <= contextPath.length() + 1) {
            return null;
        }

        String url = requestURI.substring(contextPath.length() + 1);

        if (url.isEmpty()) {
            return null;
        }

        String[] urlArray = url.split("/");

        if (urlArray.length == 0 || isReservedPath(urlArray[0])) {
            return null;
        }

        return urlArray[0];
    }

    public static boolean isForbiddenPathForBoundSchema(String boundSchema, String pathSchema) {
        return pathSchema != null && !pathSchema.equals(boundSchema);
    }

    public static Set<SchemaDTO> visibleSchemas(String boundSchema, Set<SchemaDTO> schemas) {
        if (StringUtils.isBlank(boundSchema)) {
            return schemas;
        }

        return schemas.stream()
                .filter(schema -> boundSchema.equals(schema.getSchema()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
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

    public static String buildSpaStaticBase(String contextPath) {
        return StringUtils.defaultString(contextPath) + SPA_STATIC_PATH;
    }
}

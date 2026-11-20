package biblivre.core.file;

import biblivre.core.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ETag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * Sends a {@link BiblivreFile} as the response to a GET or HEAD request, with Spring doing the HTTP
 * work: {@link ServletWebRequest#checkNotModified(String, long)} answers conditional requests,
 * {@link HttpRange} parses byte ranges, and {@link ResourceHttpMessageConverter} or {@link
 * ResourceRegionHttpMessageConverter} write the body. The file is always closed.
 */
public final class HttpFileSender {

    private static final ResourceHttpMessageConverter RESOURCE_CONVERTER =
            new ResourceHttpMessageConverter();

    private static final ResourceRegionHttpMessageConverter REGION_CONVERTER =
            new ResourceRegionHttpMessageConverter();

    private HttpFileSender() {}

    /**
     * Writes the file, or the status a conditional or range request calls for.
     *
     * @param file The file to send; null sends nothing.
     * @param request The request.
     * @param response The response.
     * @param headerOnly Whether to send the headers without the body, for HEAD requests.
     * @throws IOException If the file cannot be read or the response cannot be written.
     */
    public static void send(
            BiblivreFile file,
            HttpServletRequest request,
            HttpServletResponse response,
            boolean headerOnly)
            throws IOException {
        if (file == null) {
            return;
        }

        try (file) {
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            long lastModified = file.getLastModified();
            String etag = etag(file, lastModified);

            if (!ifMatchHolds(request, etag)) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return;
            }

            // 304 for If-None-Match / If-Modified-Since, 412 for If-Unmodified-Since.
            if (new ServletWebRequest(request, response).checkNotModified(etag, lastModified)) {
                return;
            }

            // Filters set a character encoding for every response; a binary file must not get it.
            response.reset();
            response.setBufferSize(Constants.DEFAULT_BUFFER_SIZE);

            BiblivreFileResource resource = new BiblivreFileResource(file);
            String contentType = file.getContentType();
            MediaType mediaType = mediaType(contentType);

            ServletServerHttpResponse output = new ServletServerHttpResponse(response);
            HttpHeaders headers = output.getHeaders();

            headers.setContentType(mediaType);
            headers.set(
                    HttpHeaders.CONTENT_DISPOSITION,
                    MediaHeaders.contentDisposition(
                            contentType, file.getName(), request.getHeader(HttpHeaders.ACCEPT)));
            headers.set(HttpHeaders.ACCEPT_RANGES, file.isSeekable() ? "bytes" : "none");
            headers.setETag(etag);
            headers.setLastModified(lastModified);
            headers.setExpires(System.currentTimeMillis() + Constants.DEFAULT_EXPIRE_TIME);

            List<ResourceRegion> regions;

            try {
                regions = requestedRegions(request, resource, etag, lastModified, headerOnly);
            } catch (IllegalArgumentException invalidRange) {
                response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + file.getSize());
                response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
                return;
            }

            if (!regions.isEmpty()) {
                output.setStatusCode(HttpStatus.PARTIAL_CONTENT);
                REGION_CONVERTER.write(regions, null, mediaType, output);
            } else if (headerOnly) {
                RESOURCE_CONVERTER.addDefaultHeaders(output, resource, mediaType);
            } else {
                RESOURCE_CONVERTER.write(resource, mediaType, output);
            }

            output.flush();
        }
    }

    /**
     * The regions a GET asks for, or none to send the whole file. Range is defined for GET only
     * (RFC 9110, section 14.2), is ignored when If-Range does not hold, and needs a file that can
     * seek.
     *
     * @throws IllegalArgumentException If the Range header is malformed, asks for too many ranges,
     *     or cannot be satisfied.
     */
    private static List<ResourceRegion> requestedRegions(
            HttpServletRequest request,
            BiblivreFileResource resource,
            String etag,
            long lastModified,
            boolean headerOnly) {
        String range = request.getHeader(HttpHeaders.RANGE);

        if (range == null
                || headerOnly
                || !HttpMethod.GET.matches(request.getMethod())
                || !resource.getBiblivreFile().isSeekable()
                || !ifRangeHolds(request, etag, lastModified)) {
            return List.of();
        }

        return HttpRange.toResourceRegions(HttpRange.parseRanges(range), resource);
    }

    /**
     * Spring's {@link ServletWebRequest#checkNotModified} only evaluates If-Match for unsafe
     * methods. Downloads have always answered a failing If-Match with 412, so check it here.
     */
    private static boolean ifMatchHolds(HttpServletRequest request, String etag) {
        Enumeration<String> values = request.getHeaders(HttpHeaders.IF_MATCH);

        if (values == null || !values.hasMoreElements()) {
            return true;
        }

        ETag current = ETag.create(etag);

        while (values.hasMoreElements()) {
            for (ETag requested : ETag.parse(values.nextElement())) {
                if (requested.isWildcard() || requested.compare(current, true)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Spring has no If-Range support. An entity tag must match strongly; a date holds when the file
     * has not changed since then, compared at second precision like If-Modified-Since.
     */
    private static boolean ifRangeHolds(
            HttpServletRequest request, String etag, long lastModified) {
        String ifRange = request.getHeader(HttpHeaders.IF_RANGE);

        if (ifRange == null) {
            return true;
        }

        ifRange = ifRange.trim();

        if (ifRange.startsWith("\"") || ifRange.startsWith("W/")) {
            return ETag.create(ifRange).compare(ETag.create(etag), true);
        }

        try {
            long date = request.getDateHeader(HttpHeaders.IF_RANGE);

            return date != -1 && date >= lastModified / 1000 * 1000;
        } catch (IllegalArgumentException invalidDate) {
            return false;
        }
    }

    private static String etag(BiblivreFile file, long lastModified) {
        String validator = file.getName() + "_" + file.getSize() + "_" + lastModified;

        return "\"" + DigestUtils.md5DigestAsHex(validator.getBytes(StandardCharsets.UTF_8)) + "\"";
    }

    /** The stored type with its response charset; values that are not media types download. */
    private static MediaType mediaType(String contentType) {
        try {
            return MediaType.parseMediaType(MediaHeaders.responseContentType(contentType));
        } catch (InvalidMediaTypeException invalid) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}

package biblivre.core.file;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.http.ContentDisposition;

/**
 * The Content-Type and Content-Disposition a media file is served with. Both the response that
 * streams the file through Tomcat ({@link HttpFileSender}) and the presigned redirect to the object
 * store use these, so the browser sees the same headers either way.
 */
public final class MediaHeaders {

    private MediaHeaders() {}

    /**
     * The content type a media response is served with: text gets an explicit UTF-8 charset.
     *
     * @param contentType The stored content type.
     * @return The content type to send.
     */
    public static String responseContentType(String contentType) {
        return contentType.startsWith("text") ? contentType + ";charset=UTF-8" : contentType;
    }

    /**
     * The Content-Disposition a media response is served with. Text and images open inline; any
     * other type opens inline only when the browser's Accept header takes it, else it downloads.
     * The file name is quoted and escaped; a name that is not plain ASCII also gets an RFC 5987
     * {@code filename*} parameter.
     *
     * @param contentType The stored content type.
     * @param fileName The file name offered to the browser, possibly null.
     * @param acceptHeader The request's Accept header, possibly null.
     * @return The Content-Disposition header value.
     */
    public static String contentDisposition(
            String contentType, String fileName, String acceptHeader) {
        boolean inline =
                contentType.startsWith("text")
                        || contentType.startsWith("image")
                        || (acceptHeader != null && accepts(acceptHeader, contentType));

        ContentDisposition.Builder builder =
                inline ? ContentDisposition.inline() : ContentDisposition.attachment();

        if (fileName != null) {
            if (StandardCharsets.US_ASCII.newEncoder().canEncode(fileName)) {
                builder.filename(fileName);
            } else {
                builder.filename(fileName, StandardCharsets.UTF_8);
            }
        }

        return builder.build().toString();
    }

    /**
     * Returns true if the given accept header accepts the given value.
     *
     * @param acceptHeader The accept header.
     * @param toAccept The value to be accepted.
     * @return True if the given accept header accepts the given value.
     */
    private static boolean accepts(String acceptHeader, String toAccept) {
        if (toAccept.equals("x-download")) {
            return false;
        }

        String[] acceptValues = acceptHeader.split("\\s*[,;]\\s*");
        Arrays.sort(acceptValues);
        return Arrays.binarySearch(acceptValues, toAccept) > -1
                || Arrays.binarySearch(acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1
                || Arrays.binarySearch(acceptValues, "*/*") > -1;
    }
}

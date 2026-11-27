package biblivre.core.monitoring;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;

/**
 * A parsed Sentry DSN ({@code scheme://publicKey@host[:port][/path]/projectId}).
 *
 * @param raw the DSN as configured, without a trailing slash
 * @param publicKey the key used to authenticate the envelope with the ingest server
 * @param envelopeUri where envelopes for this project are posted
 */
public record SentryDsn(String raw, String publicKey, URI envelopeUri) {

    public static SentryDsn parse(String dsn) {
        String normalized = normalize(dsn);

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Empty DSN");
        }

        URI uri;

        try {
            uri = new URI(normalized);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid DSN", e);
        }

        String userInfo = uri.getUserInfo();
        String path = uri.getPath();

        if (uri.getScheme() == null
                || uri.getHost() == null
                || StringUtils.isBlank(userInfo)
                || StringUtils.isBlank(path)) {
            throw new IllegalArgumentException("DSN must look like scheme://key@host/projectId");
        }

        String publicKey = StringUtils.substringBefore(userInfo, ":");
        int lastSlash = path.lastIndexOf('/');
        String projectId = path.substring(lastSlash + 1);
        String prefix = path.substring(0, lastSlash);

        if (projectId.isEmpty()) {
            throw new IllegalArgumentException("DSN is missing the project id");
        }

        try {
            URI envelopeUri =
                    new URI(
                            uri.getScheme(),
                            null,
                            uri.getHost(),
                            uri.getPort(),
                            prefix + "/api/" + projectId + "/envelope/",
                            null,
                            null);

            return new SentryDsn(normalized, publicKey, envelopeUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid DSN", e);
        }
    }

    /**
     * Whether {@code other} names the same DSN, ignoring surrounding blanks and a trailing slash.
     */
    public boolean matches(String other) {
        return raw.equals(normalize(other));
    }

    private static String normalize(String dsn) {
        return StringUtils.removeEnd(StringUtils.trimToEmpty(dsn), "/");
    }
}

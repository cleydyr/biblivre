package biblivre.core.monitoring;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.utils.HtmlSafeJson;
import biblivre.login.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Clock;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

/**
 * Configures the browser Sentry SDK and relays its envelopes to GlitchTip (see
 * docs/client-error-monitoring.md).
 */
@Slf4j
@Service
public class ClientMonitoringService {

    public static final String TUNNEL_PATH = "/api/v2/client-errors";

    private static final long WARN_INTERVAL_MILLIS = 60_000;

    private final ClientMonitoringProperties properties;
    private final SentryDsn dsn;
    private final String release;
    private final HttpClient httpClient;
    private final Clock clock;
    private final AtomicLong lastForwardWarning = new AtomicLong();

    @Autowired
    public ClientMonitoringService(
            ClientMonitoringProperties properties,
            ObjectProvider<BuildProperties> buildProperties) {
        this(
                properties,
                buildProperties.getIfAvailable(),
                HttpClient.newBuilder().connectTimeout(properties.getConnectTimeout()).build(),
                Clock.systemUTC());
    }

    ClientMonitoringService(
            ClientMonitoringProperties properties,
            BuildProperties buildProperties,
            HttpClient httpClient,
            Clock clock) {
        this.properties = properties;
        this.dsn = parseDsn(properties.getDsn());
        this.release = buildProperties != null ? "biblivre@" + buildProperties.getVersion() : null;
        this.httpClient = httpClient;
        this.clock = clock;
    }

    public boolean isEnabled() {
        return dsn != null;
    }

    public SentryDsn getDsn() {
        return dsn;
    }

    public ClientMonitoringProperties getProperties() {
        return properties;
    }

    /**
     * The browser SDK settings for the current request, as a JavaScript literal safe to embed in a
     * {@code <script>} block. Renders {@code {}} when monitoring is disabled, which the browser
     * reads as "do not initialise".
     */
    public String clientConfigJson(HttpServletRequest request) {
        if (!isEnabled()) {
            return "{}";
        }

        JSONObject config = new JSONObject();
        String schema = SchemaThreadLocal.get();

        config.put("dsn", dsn.raw());
        config.put("tunnel", request.getContextPath() + TUNNEL_PATH);
        config.put("environment", properties.getEnvironment());
        config.put("sampleRate", properties.getSampleRate());

        if (release != null) {
            config.put("release", release);
        }

        if (StringUtils.isNotBlank(schema)) {
            config.put("schema", schema);
        }

        int userId = loggedUserId(request, schema);

        if (userId > 0) {
            config.put("userId", String.valueOf(userId));
        }

        return HtmlSafeJson.stringify(config);
    }

    /** Sends the envelope to GlitchTip without waiting for the answer. */
    public void forward(byte[] envelope, String clientIp) {
        HttpRequest.Builder request =
                HttpRequest.newBuilder(dsn.envelopeUri())
                        .timeout(properties.getForwardTimeout())
                        .header("Content-Type", "application/x-sentry-envelope")
                        .header(
                                "X-Sentry-Auth",
                                "Sentry sentry_version=7, sentry_client=biblivre-tunnel/1.0,"
                                        + " sentry_key="
                                        + dsn.publicKey())
                        .POST(HttpRequest.BodyPublishers.ofByteArray(envelope));

        if (StringUtils.isNotBlank(clientIp)) {
            request.header("X-Forwarded-For", clientIp);
        }

        httpClient
                .sendAsync(request.build(), HttpResponse.BodyHandlers.discarding())
                .whenComplete(
                        (response, error) -> {
                            if (error != null) {
                                warnThrottled("Could not forward client error envelope: {}", error);
                            } else if (response.statusCode() >= 400) {
                                warnThrottled(
                                        "Error monitoring server rejected client envelope with"
                                                + " HTTP {}",
                                        response.statusCode());
                            }
                        });
    }

    private void warnThrottled(String message, Object argument) {
        long now = clock.millis();
        long last = lastForwardWarning.get();

        if (now - last >= WARN_INTERVAL_MILLIS && lastForwardWarning.compareAndSet(last, now)) {
            log.warn(
                    message,
                    argument instanceof Throwable t ? String.valueOf(t.getMessage()) : argument);
        }
    }

    private static int loggedUserId(HttpServletRequest request, String schema) {
        HttpSession session = request.getSession(false);

        if (session == null || schema == null) {
            return 0;
        }

        return session.getAttribute(schema + ".logged_user") instanceof LoginDTO login
                ? login.getId()
                : 0;
    }

    private static SentryDsn parseDsn(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return SentryDsn.parse(value);
        } catch (IllegalArgumentException e) {
            log.error("Ignoring invalid biblivre.client-monitoring.dsn: {}", e.getMessage());
            return null;
        }
    }
}

package biblivre.core.monitoring;

import biblivre.core.utils.NetworkUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Same-origin relay for browser Sentry SDK envelopes (the SDK's {@code tunnel} option). It only
 * forwards envelopes addressed to the configured DSN, so it cannot be used as an open proxy.
 */
@Slf4j
@RestController
public class ClientErrorTunnelController {

    /**
     * GlitchTip does not ingest session replays, and release-health sessions are not used; drop
     * them instead of forwarding.
     */
    static final Set<String> DROPPED_ITEM_TYPES =
            Set.of("replay_event", "replay_recording", "session", "sessions");

    private static final String GLOBAL_KEY = "*";

    private final ClientMonitoringService monitoringService;
    private final FixedWindowRateLimiter clientLimiter;
    private final FixedWindowRateLimiter globalLimiter;

    @Autowired
    public ClientErrorTunnelController(ClientMonitoringService monitoringService) {
        this(monitoringService, Clock.systemUTC());
    }

    ClientErrorTunnelController(ClientMonitoringService monitoringService, Clock clock) {
        this.monitoringService = monitoringService;
        this.clientLimiter = new FixedWindowRateLimiter(clock);
        this.globalLimiter = new FixedWindowRateLimiter(clock);
    }

    @PostMapping(ClientMonitoringService.TUNNEL_PATH)
    public ResponseEntity<Void> tunnel(HttpServletRequest request) throws IOException {
        if (!monitoringService.isEnabled()) {
            return ResponseEntity.notFound().build();
        }

        ClientMonitoringProperties properties = monitoringService.getProperties();
        int maxBytes = properties.getMaxEnvelopeBytes();

        if (request.getContentLengthLong() > maxBytes) {
            return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).build();
        }

        String clientIp = NetworkUtils.remoteIpAddress(request);

        if (!clientLimiter.tryAcquire(clientIp, properties.getClientEnvelopesPerMinute())
                || !globalLimiter.tryAcquire(
                        GLOBAL_KEY, properties.getGlobalEnvelopesPerMinute())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        byte[] body;

        try (InputStream in = request.getInputStream()) {
            body = in.readNBytes(maxBytes + 1);
        }

        if (body.length > maxBytes) {
            return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).build();
        }

        SentryEnvelope envelope;

        try {
            envelope = SentryEnvelope.parse(body);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        if (!monitoringService.getDsn().matches(envelope.dsn())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SentryEnvelope forwarded = envelope.withoutItemTypes(DROPPED_ITEM_TYPES);

        if (!forwarded.isEmpty()) {
            monitoringService.forward(forwarded.toBytes(), clientIp);
        }

        return ResponseEntity.ok().build();
    }
}

package biblivre.core.monitoring;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Browser error monitoring (see docs/client-error-monitoring.md). Disabled while {@code dsn} is
 * blank.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "biblivre.client-monitoring")
public class ClientMonitoringProperties {
    /** Sentry-compatible DSN of the GlitchTip project, e.g. https://key@host/1. */
    private String dsn = "";

    /** Reported as the event environment, e.g. production or staging. */
    private String environment = "production";

    /** Fraction of error events the browser SDK sends (0.0 - 1.0). */
    private double sampleRate = 1.0;

    /** Largest envelope the tunnel accepts. */
    private int maxEnvelopeBytes = 1024 * 1024;

    /** Envelopes accepted per client IP per minute. */
    private int clientEnvelopesPerMinute = 60;

    /** Envelopes accepted per minute across all clients, to cap load on GlitchTip. */
    private int globalEnvelopesPerMinute = 1200;

    private Duration connectTimeout = Duration.ofSeconds(2);

    private Duration forwardTimeout = Duration.ofSeconds(10);
}

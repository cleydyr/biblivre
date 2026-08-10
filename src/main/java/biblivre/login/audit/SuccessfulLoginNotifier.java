package biblivre.login.audit;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.utils.NetworkUtils;
import biblivre.login.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Explicit hook: enqueue a Successful Login for the Login Audit sidecar without blocking the
 * Biblivre session. Delivery is drained asynchronously by {@link SuccessfulLoginOutboxDispatcher}.
 */
@Component
public class SuccessfulLoginNotifier {

    private static final Logger log = LoggerFactory.getLogger(SuccessfulLoginNotifier.class);

    private final SuccessfulLoginOutboxDAO outboxDAO;
    private final boolean enabled;

    public SuccessfulLoginNotifier(
            SuccessfulLoginOutboxDAO outboxDAO,
            @Value("${login-audit.enabled:false}") boolean enabled) {
        this.outboxDAO = outboxDAO;
        this.enabled = enabled;
    }

    public void notifySuccessfulLogin(LoginDTO login, HttpServletRequest request) {
        if (!enabled) {
            return;
        }

        try {
            SuccessfulLoginNotification notification =
                    new SuccessfulLoginNotification(
                            UUID.randomUUID(),
                            SchemaThreadLocal.get(),
                            login.getId(),
                            login.getLogin(),
                            NetworkUtils.remoteIpAddress(request),
                            Instant.now());

            outboxDAO.enqueue(notification);
        } catch (Exception e) {
            // Must not break login; dispatcher/ops can alert on enqueue failures via logs.
            log.error("Failed to enqueue Successful Login for Login Audit", e);
        }
    }
}

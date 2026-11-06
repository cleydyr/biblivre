package biblivre.login.audit;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(name = "login-audit.enabled", havingValue = "true")
@Slf4j
public class SuccessfulLoginOutboxDispatcher {

    private final SuccessfulLoginOutboxDAO outboxDAO;
    private final RestClient restClient;
    private final String ingestUrl;
    private final String apiKey;

    public SuccessfulLoginOutboxDispatcher(
            SuccessfulLoginOutboxDAO outboxDAO,
            RestClient.Builder restClientBuilder,
            @Value("${login-audit.ingest-url}") String ingestUrl,
            @Value("${login-audit.api-key}") String apiKey) {
        this.outboxDAO = outboxDAO;
        this.restClient = restClientBuilder.build();
        this.ingestUrl = ingestUrl;
        this.apiKey = apiKey;
    }

    @Scheduled(fixedDelayString = "${login-audit.dispatch-delay-ms:5000}")
    public void dispatch() {
        List<SuccessfulLoginNotification> pending = outboxDAO.listPending(50);

        for (SuccessfulLoginNotification notification : pending) {
            try {
                restClient
                        .post()
                        .uri(ingestUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Login-Audit-Key", apiKey)
                        .body(notification)
                        .retrieve()
                        .toBodilessEntity();

                outboxDAO.markSent(notification.occurrenceId());
            } catch (Exception e) {
                log.warn(
                        "Failed to deliver Successful Login {} to Login Audit: {}",
                        notification.occurrenceId(),
                        e.toString());
                outboxDAO.incrementAttempts(notification.occurrenceId());
            }
        }
    }
}

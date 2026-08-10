package biblivre.login.audit;

import java.time.Instant;
import java.util.UUID;

public record SuccessfulLoginNotification(
        UUID occurrenceId,
        String schema,
        int loginId,
        String username,
        String sourceAddress,
        Instant occurredAt) {}

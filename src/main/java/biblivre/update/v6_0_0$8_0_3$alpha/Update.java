package biblivre.update.v6_0_0$8_0_3$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    @Override
    public void doUpdate(Connection connection) throws UpdateException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS login_audit_outbox (
                        occurrence_id UUID PRIMARY KEY,
                        schema_name VARCHAR(64) NOT NULL,
                        login_id INTEGER NOT NULL,
                        username VARCHAR(255) NOT NULL,
                        source_address VARCHAR(128) NOT NULL,
                        occurred_at TIMESTAMPTZ NOT NULL,
                        attempts INTEGER NOT NULL DEFAULT 0,
                        last_error_at TIMESTAMPTZ,
                        sent_at TIMESTAMPTZ,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT now()
                    )
                    """);
            statement.execute(
                    """
                    CREATE INDEX IF NOT EXISTS login_audit_outbox_pending_idx
                        ON login_audit_outbox (occurred_at)
                        WHERE sent_at IS NULL
                    """);
        } catch (Exception e) {
            throw new UpdateException("failed to create login_audit_outbox", e);
        }
    }
}

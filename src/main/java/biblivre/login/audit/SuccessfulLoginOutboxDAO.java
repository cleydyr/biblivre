package biblivre.login.audit;

import biblivre.core.AbstractDAO;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SuccessfulLoginOutboxDAO extends AbstractDAO {

    public void enqueue(SuccessfulLoginNotification notification) {
        SchemaThreadLocal.withGlobalSchema(() -> doEnqueue(notification));
    }

    public List<SuccessfulLoginNotification> listPending(int limit) {
        return SchemaThreadLocal.withGlobalSchema(() -> doListPending(limit));
    }

    public void markSent(UUID occurrenceId) {
        SchemaThreadLocal.withGlobalSchema(() -> doMarkSent(occurrenceId));
    }

    public void incrementAttempts(UUID occurrenceId) {
        SchemaThreadLocal.withGlobalSchema(() -> doIncrementAttempts(occurrenceId));
    }

    private void doEnqueue(SuccessfulLoginNotification notification) {
        String sql =
                """
                INSERT INTO login_audit_outbox (
                    occurrence_id, schema_name, login_id, username,
                    source_address, occurred_at, attempts
                ) VALUES (?, ?, ?, ?, ?, ?, 0)
                ON CONFLICT (occurrence_id) DO NOTHING
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setObject(1, notification.occurrenceId());
            pst.setString(2, notification.schema());
            pst.setInt(3, notification.loginId());
            pst.setString(4, notification.username());
            pst.setString(5, notification.sourceAddress());
            pst.setTimestamp(6, Timestamp.from(notification.occurredAt()));

            pst.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private List<SuccessfulLoginNotification> doListPending(int limit) {
        String sql =
                """
                SELECT occurrence_id, schema_name, login_id, username, source_address, occurred_at
                FROM login_audit_outbox
                WHERE sent_at IS NULL
                ORDER BY occurred_at ASC
                LIMIT ?
                """;

        List<SuccessfulLoginNotification> result = new ArrayList<>();

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, limit);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                result.add(
                        new SuccessfulLoginNotification(
                                (UUID) rs.getObject("occurrence_id"),
                                rs.getString("schema_name"),
                                rs.getInt("login_id"),
                                rs.getString("username"),
                                rs.getString("source_address"),
                                rs.getTimestamp("occurred_at").toInstant()));
            }

            return result;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private void doMarkSent(UUID occurrenceId) {
        String sql = "UPDATE login_audit_outbox SET sent_at = ? WHERE occurrence_id = ?";

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.from(Instant.now()));
            pst.setObject(2, occurrenceId);
            pst.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    private void doIncrementAttempts(UUID occurrenceId) {
        String sql =
                """
                UPDATE login_audit_outbox
                SET attempts = attempts + 1, last_error_at = ?
                WHERE occurrence_id = ?
                """;

        try (Connection con = datasource.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setTimestamp(1, Timestamp.from(Instant.now()));
            pst.setObject(2, occurrenceId);
            pst.executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }
}

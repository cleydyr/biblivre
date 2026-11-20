package biblivre.digitalmedia;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

/**
 * Pending direct uploads of the current library. Expiry is computed and compared in the database
 * clock so the JVM time zone can't skew it.
 */
@Service
public class PendingUploadDAO extends AbstractDAO {

    private static final String COLUMNS = "id, storage_key, name, content_type, size, created_by";

    public void insert(PendingUpload upload, Duration timeToLive) {
        try (Connection con = datasource.getConnection();
                PreparedStatement pst =
                        con.prepareStatement(
                                """
                                INSERT INTO digital_media_pending_upload
                                    (id, storage_key, name, content_type, size, created_by,
                                     expires_at)
                                VALUES (?, ?, ?, ?, ?, ?, localtimestamp + make_interval(secs => ?))
                                """)) {
            pst.setObject(1, upload.id());
            pst.setString(2, upload.storageKey());
            pst.setString(3, upload.name());
            pst.setString(4, upload.contentType());
            pst.setLong(5, upload.size());
            pst.setInt(6, upload.createdBy());
            pst.setLong(7, timeToLive.toSeconds());

            pst.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /** The upload, if it exists and hasn't expired. */
    public Optional<PendingUpload> findActive(UUID id) {
        try (Connection con = datasource.getConnection();
                PreparedStatement pst =
                        con.prepareStatement(
                                "SELECT "
                                        + COLUMNS
                                        + " FROM digital_media_pending_upload"
                                        + " WHERE id = ? AND expires_at > localtimestamp")) {
            pst.setObject(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? Optional.of(read(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    /**
     * Turns the upload into a {@code digital_media} row and drops the pending one, atomically. Only
     * one caller wins a race on the same upload; the others get empty.
     *
     * @return the new {@code digital_media} id
     */
    public Optional<Integer> complete(PendingUpload upload) {
        return withTransactionContext(
                con -> {
                    try (PreparedStatement delete =
                            con.prepareStatement(
                                    "DELETE FROM digital_media_pending_upload"
                                            + " WHERE id = ? AND expires_at > localtimestamp")) {
                        delete.setObject(1, upload.id());

                        if (delete.executeUpdate() == 0) {
                            return Optional.<Integer>empty();
                        }
                    }

                    try (PreparedStatement insert =
                            con.prepareStatement(
                                    """
                                    INSERT INTO digital_media
                                        (id, name, blob, storage_key, content_type, size,
                                         created_by)
                                    VALUES (nextval('digital_media_id_seq'), ?, NULL, ?, ?, ?, ?)
                                    RETURNING id
                                    """)) {
                        insert.setString(1, upload.name());
                        insert.setString(2, upload.storageKey());
                        insert.setString(3, upload.contentType());
                        insert.setLong(4, upload.size());
                        insert.setInt(5, upload.createdBy());

                        try (ResultSet rs = insert.executeQuery()) {
                            rs.next();

                            return Optional.of(rs.getInt(1));
                        }
                    }
                });
    }

    public void delete(UUID id) {
        try (Connection con = datasource.getConnection();
                PreparedStatement pst =
                        con.prepareStatement(
                                "DELETE FROM digital_media_pending_upload WHERE id = ?")) {
            pst.setObject(1, id);

            pst.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<PendingUpload> listExpired() {
        List<PendingUpload> expired = new ArrayList<>();

        try (Connection con = datasource.getConnection();
                PreparedStatement pst =
                        con.prepareStatement(
                                "SELECT "
                                        + COLUMNS
                                        + " FROM digital_media_pending_upload"
                                        + " WHERE expires_at <= localtimestamp");
                ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                expired.add(read(rs));
            }

            return expired;
        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    private static PendingUpload read(ResultSet rs) throws SQLException {
        return new PendingUpload(
                rs.getObject("id", UUID.class),
                rs.getString("storage_key"),
                rs.getString("name"),
                rs.getString("content_type"),
                rs.getLong("size"),
                rs.getInt("created_by"));
    }
}

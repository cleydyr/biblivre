package biblivre.update.v6_0_0$9_0_11$alpha;

import biblivre.digitalmedia.DigitalMediaSchema;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.stereotype.Component;

/**
 * Media stored in S3 no longer needs a large object: {@code digital_media.storage_key} names the
 * object in its store and {@code blob} becomes optional. Existing rows keep resolving because their
 * key is the large object id they were stored under. Direct uploads wait in {@code
 * digital_media_pending_upload} until the object is confirmed.
 */
@Component
public class Update implements UpdateService {

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            for (String sql : DigitalMediaSchema.STORAGE_KEY_COLUMN) {
                statement.execute(sql);
            }

            statement.execute(DigitalMediaSchema.BACKFILL_STORAGE_KEY);
            statement.execute(DigitalMediaSchema.PENDING_UPLOAD_TABLE);
            statement.execute(DigitalMediaSchema.PENDING_UPLOAD_EXPIRES_INDEX);
        } catch (SQLException e) {
            throw new UpdateException("Error adding digital media storage keys", e);
        }
    }
}

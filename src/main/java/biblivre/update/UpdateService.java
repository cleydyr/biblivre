package biblivre.update;

import java.sql.Connection;

public interface UpdateService {
    public default void doUpdate(Connection connection) {
        // Do nothing
    }

    public default void doUpdateScopedBySchema(Connection connection) {
        // Do nothing
    }

    public default void afterUpdate() {
        // Do nothing
    }
}

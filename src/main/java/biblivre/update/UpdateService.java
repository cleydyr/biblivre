package biblivre.update;

import java.sql.Connection;

public interface UpdateService {
    default void doUpdate(Connection connection) {
        // Do nothing
    }

    default void doUpdateScopedBySchema(Connection connection) {
        // Do nothing
    }

    default void afterUpdate() {
        // Do nothing
    }
}

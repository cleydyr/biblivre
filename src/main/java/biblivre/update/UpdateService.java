package biblivre.update;

import java.sql.Connection;
import java.sql.SQLException;

public interface UpdateService {
    public default void doUpdate(Connection connection) throws SQLException {
        // Do nothing
    }

    public default void doUpdateScopedBySchema(Connection connection) throws SQLException {
        // Do nothing
    }

    public default void afterUpdate() {
        // Do nothing
    }
}

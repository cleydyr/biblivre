package biblivre.update.v6_0_0$2_0_0$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    @Override
    public void doUpdate(Connection connection) {
        addSaltColumnToLoginTable(connection);
    }

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        addSaltColumnToLoginTable(connection);
    }

    private void addSaltColumnToLoginTable(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_SALT_COLUMN_SQL)) {
            statement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new UpdateException("can't alter login table", sqlException);
        }
    }

    private static final String ADD_SALT_COLUMN_SQL =
            "ALTER TABLE logins ADD COLUMN IF NOT EXISTS password_salt bytea";
}

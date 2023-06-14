package biblivre.update.v6_0_0$3_0_0$alpha;

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
        addBytePasswordColumnToLoginTable(connection);
    }

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        addBytePasswordColumnToLoginTable(connection);
        dropNonNullConstraintFromPasswordColumn(connection);
    }

    private void addBytePasswordColumnToLoginTable(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_SALT_COLUMN_SQL)) {
            statement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new UpdateException("can't alter logins table", sqlException);
        }
    }

    private void dropNonNullConstraintFromPasswordColumn(Connection connection) {
        try (PreparedStatement statement =
                connection.prepareStatement(REMOVE_NON_NULL_CONSTRAINT_FROM_PASSWORD)) {
            statement.executeUpdate();
        } catch (SQLException sqlException) {
            throw new UpdateException("can't alter logins table", sqlException);
        }
    }

    private static final String ADD_SALT_COLUMN_SQL =
            "ALTER TABLE logins ADD COLUMN IF NOT EXISTS salted_password bytea";

    private static final String REMOVE_NON_NULL_CONSTRAINT_FROM_PASSWORD =
            "ALTER TABLE logins ALTER COLUMN password DROP NOT NULL";
}

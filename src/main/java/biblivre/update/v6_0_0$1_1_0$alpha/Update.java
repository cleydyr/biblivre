package biblivre.update.v6_0_0$1_1_0$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    public void doUpdate(Connection connection) throws UpdateException {
        _deleteUnlinkFunctionCascade(connection);
    }

    private void _deleteUnlinkFunctionCascade(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(_DROP_FUNCTION_SQL);
        } catch (SQLException sqlException) {
            throw new UpdateException("can't delete unlink function", sqlException);
        }
    }

    private static final String _DROP_FUNCTION_SQL =
            "DROP FUNCTION IF EXISTS global.unlink CASCADE";
}

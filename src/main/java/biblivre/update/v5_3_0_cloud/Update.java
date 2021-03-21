package biblivre.update.v5_3_0_cloud;

import biblivre.update.UpdateService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Update implements UpdateService {

    public void doUpdate(Connection connection) throws SQLException {
        _deleteUnlinkFunctionCascade(connection);
    }

    @Override
    public String getVersion() {
        return "5.3.0";
    }

    private void _deleteUnlinkFunctionCascade(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(_DROP_FUNCTION_SQL);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final String _DROP_FUNCTION_SQL =
            "DROP FUNCTION IF EXISTS global.unlink CASCADE";
}

package biblivre.update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import biblivre.core.PreparedStatementUtil;

public interface UpdateService {
	public String getVersion();

	public default void doUpdate(Connection connection) throws SQLException {
		// Do nothing
	}

	public default void doUpdateScopedBySchema(Connection connection) throws SQLException {
		// Do nothing
	}

	public default void afterUpdate() {
		// Do nothing
	}

	public static boolean checkColumnExistence(
			String tableName, String columnName, Connection connection) throws SQLException {

		String checkExistenceSQLTemplate =
				"SELECT count(*) as count FROM information_schema.columns WHERE table_schema = ? " +
					"and table_name = ? and column_name = ?;";

		try (PreparedStatement pst = connection.prepareStatement(checkExistenceSQLTemplate)) {

			PreparedStatementUtil.setAllParameters(
					pst, connection.getSchema(), tableName, columnName);

			try (ResultSet rs = pst.executeQuery()) {
				return rs.next() && rs.getInt("count") == 1;
			}
		}
	}
}

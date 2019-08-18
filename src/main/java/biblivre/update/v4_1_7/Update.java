package biblivre.update.v4_1_7;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		_fixHoldingCreationTable(connection);

		_fixCDDBiblioBriefFormat(connection);
	}

	private static void _fixCDDBiblioBriefFormat(Connection connection) throws SQLException {
		_executeStatement(connection, _UPDATE_BIBLIO_BRIEF_FORMATS_SQL);
	}

	public static void _executeStatement(Connection connection, String sql) throws SQLException {
		try (Statement st = connection.createStatement()) {
			st.execute(sql);
		}
	}

	private static void _fixHoldingCreationTable(Connection connection) throws SQLException {
		_executeStatement(connection, _UPDATE_HOLDING_CREATION_COUNTER_SQL);
	}

	@Override
	public String getVersion() {
		return "4.1.7";
	}

	private static final String _UPDATE_HOLDING_CREATION_COUNTER_SQL =
			"UPDATE holding_creation_counter HA " +
				"SET user_name = coalesce(U.name, L.login), user_login = L.login " +
				"FROM holding_creation_counter H " +
				"INNER JOIN logins L ON L.id = H.created_by " +
				"LEFT JOIN users U on U.login_id = H.created_by " +
				"WHERE HA.created_by = H.created_by;";

	private static final String _UPDATE_BIBLIO_BRIEF_FORMATS_SQL =
			"UPDATE biblio_brief_formats " +
					"SET format = '${a}_{ }${2}' " +
					"WHERE format = '${a}_{ }_{2}';";
}

package biblivre.update.v4_1_11a;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		_fixAuthoritiesBriefFormat(connection);
	}

	@Override
	public String getVersion() {
		return "4.1.11a";
	}

	private void _fixAuthoritiesBriefFormat(Connection connection) throws SQLException {
		String updateSQL = "UPDATE authorities_brief_formats " +
				"SET format = '${a}_{; }${b}_{; }${c}_{ - }${d}' " +
				"WHERE datafield = '110';";

		try (Statement update = connection.createStatement()) {
			update.execute(updateSQL);
		}
	}
}

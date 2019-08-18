package biblivre.update.v4_0_5b;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.core.PreparedStatementUtil;
import biblivre.core.utils.TextUtils;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		if (UpdateService.checkColumnExistence("users", "name_ascii", connection)) {
			return;
		}

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("ALTER TABLE users ADD COLUMN name_ascii character varying;");
		}

		try (Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("SELECT id, name FROM users;");
				PreparedStatement pst = connection.prepareStatement(
						"UPDATE users SET name_ascii = ? WHERE id = ?");
				) {

			if (rs.next()) {
				do {
					PreparedStatementUtil.setAllParameters(
							pst, TextUtils.removeDiacriticals(rs.getString("name")),
							rs.getInt("id"));

					pst.addBatch();
				} while (rs.next());

				pst.executeBatch();
			}
		}
	}

	@Override
	public String getVersion() {
		return "4.0.5b";
	}

}

package biblivre.update.v5_0_4c;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import biblivre.core.PreparedStatementUtil;
import biblivre.update.UpdateService;

public class Update implements UpdateService {


	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		_updateZ3950Address(connection, "Library of Congress Online Catalog - EUA", "lx2.loc.gov");
	}

	@Override
	public String getVersion() {
		return "5.0.4c";
	}

	private void _updateZ3950Address(Connection connection, String name, String url)
		throws SQLException {

		String sql = "UPDATE z3950_addresses SET url = ? WHERE name = ?;";

		try (PreparedStatement pst = connection.prepareStatement(sql)) {
			PreparedStatementUtil.setAllParameters(pst, url, name);

			pst.execute();
		}
	}
}

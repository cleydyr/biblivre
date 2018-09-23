package biblivre.core.update;

import java.sql.SQLException;

public class UpdateBO_4_1_7 extends UpdateBO {

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {

		dao.fixHoldingCreationTable();
		dao.fixCDDBiblioBriefFormat();
	}

	@Override
	protected String getVersion() {
		return "4.1.7";
	}
}

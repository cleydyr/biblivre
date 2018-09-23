package biblivre.core.update;

import java.sql.SQLException;

public class UpdateBO_4_1_11_a extends UpdateBO {

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {

		dao.fixAuthoritiesBriefFormat();
	}

	@Override
	protected String getVersion() {
		return "4.1.11";
	}
}

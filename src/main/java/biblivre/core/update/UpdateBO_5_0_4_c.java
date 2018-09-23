package biblivre.core.update;

import java.sql.SQLException;

public class UpdateBO_5_0_4_c extends UpdateBO {

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {
		dao.updateZ3950Address("Library of Congress Online Catalog - EUA", "lx2.loc.gov");
	}

	@Override
	protected String getVersion() {
		return "5.0.4c";
	}
}

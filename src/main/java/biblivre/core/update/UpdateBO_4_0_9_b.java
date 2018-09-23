package biblivre.core.update;

import java.sql.SQLException;

public class UpdateBO_4_0_9_b extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		dao.fixUpdateTranslationFunction();

		dao.fixUpdateUserFunction();

		if (!dao.checkTableExistance("backups")) {
			dao.fixBackupTable();
		}
	}

	@Override
	protected String getVersion() {
		return "4.0.9b";
	}
}

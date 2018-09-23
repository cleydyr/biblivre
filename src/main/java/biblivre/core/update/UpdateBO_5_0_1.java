package biblivre.core.update;

import java.sql.SQLException;

import biblivre.core.configurations.Configurations;
import biblivre.core.translations.Translations;

public class UpdateBO_5_0_1 extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		dao.replaceBiblivreVersion();

		Translations.reset();
		Configurations.reset();
	}

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema) throws SQLException {
		dao.replaceBiblivreVersion();

		Translations.reset();
		Configurations.reset();
	}

	@Override
	protected String getVersion() {
		return "5.0.1";
	}
}

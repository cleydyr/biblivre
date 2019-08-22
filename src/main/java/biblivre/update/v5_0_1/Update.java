package biblivre.update.v5_0_1;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import biblivre.core.configurations.Configurations;
import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) throws SQLException {
		_replaceBiblivreVersion(connection);
	}

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		_replaceBiblivreVersion(connection);
	}

	@Override
	public void afterUpdate() {
		Translations.reset();

		Configurations.reset();
	}

	@Override
	public String getVersion() {
		return "5.0.1";
	}

	private void _replaceBiblivreVersion(Connection con)  throws SQLException {
		try (Statement batchUpdateStatement = con.createStatement()) {

			_addUpdateTranslationsToBatch(batchUpdateStatement);

			_addUpdateConfigurationsToBatch(batchUpdateStatement);

			batchUpdateStatement.executeBatch();
		}
	}

	private void _addUpdateConfigurationsToBatch(Statement batchUpdateStatement) {
		_CONFIGURATIONS_ORIGINAL_REPLACEMENT_PAIRS.forEach(
				(original, replacement) -> {
					try {
						String updateSQL = String.format(
								_CONFIGURATIONS_UPDATE_TEMPLATE, original, replacement, original);

						batchUpdateStatement.addBatch(updateSQL);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		);
	}

	private void _addUpdateTranslationsToBatch(Statement batchUpdateStatement) {
		_TRANSLATIONS_ORIGINAL_REPLACEMENT_PAIRS.forEach(
				(original, replacement) -> {
					try {
						String updateSQL = String.format(
								_TRANSLATIONS_UPDATE_TEMPLATE, original, replacement, original);

						batchUpdateStatement.addBatch(updateSQL);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		);
	}

	@SuppressWarnings("serial")
	private static final Map<String, String> _CONFIGURATIONS_ORIGINAL_REPLACEMENT_PAIRS =
			new HashMap<String, String>() {{
				put("Biblivre IV", "Biblivre V");
				put("ersão 4.0", "ersão 5.0");
				put("ersión 4.0", "ersión 5.0");
				put("ersion 4.0", "ersion 5.0");
			}};

	@SuppressWarnings("serial")
	private static final Map<String, String> _TRANSLATIONS_ORIGINAL_REPLACEMENT_PAIRS =
			new HashMap<String, String>() {{
				put("Biblivre 4", "Biblivre 5");
				put("Biblivre4", "Biblivre 5");
				put("Biblivre IV", "Biblivre V");
				put("ersão 4.0", "ersão 5.0");
				put("ersión 4.0", "ersión 5.0");
				put("ersion 4.0", "ersion 5.0");
			}};

	private static final String _CONFIGURATIONS_UPDATE_TEMPLATE =
			"UPDATE configurations SET value = replace(value, '%s', '%s'), modified = now() " +
				"WHERE value like '%%%s%%'";

	private static final String _TRANSLATIONS_UPDATE_TEMPLATE =
			"UPDATE translations SET text = replace(text, '%s', '%S'), modified = now() " +
				"WHERE text like '%%%s%%';";
}

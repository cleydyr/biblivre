package biblivre.update.v4_1_10a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.PreparedStatementUtil;
import biblivre.core.translations.Translations;
import biblivre.core.utils.StringPool;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.tab.record.custom.field_label.biblio_501", "Notas");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.tab.record.custom.field_label.biblio_501", "Notes");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.tab.record.custom.field_label.biblio_501", "Notas");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.tab.record.custom.field_label.biblio_530", "Notas");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.tab.record.custom.field_label.biblio_530", "Notes");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.tab.record.custom.field_label.biblio_530", "Notas");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.tab.record.custom.field_label.biblio_595", "Notas");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.tab.record.custom.field_label.biblio_595", "Notes");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.tab.record.custom.field_label.biblio_595", "Notas");
	}

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		Fields.updateBriefFormat(connection, RecordType.BIBLIO, "490", "(${a}_{ ; }${v})");

		Fields.updateBriefFormat(connection, RecordType.BIBLIO, "830", "${a}_{. }${p}_{ ; }${v}");

		_invalidateIndex(connection, RecordType.BIBLIO);

		Fields.reset(connection.getSchema(), RecordType.BIBLIO);
	}

	private void _invalidateIndex(Connection connection, RecordType recordType)
		throws SQLException {

		StringBuilder deleteSQL =
				new StringBuilder(3)
					.append("DELETE FROM ")
					.append(recordType)
					.append("_idx_sort WHERE record_id = 0;");

		try (Statement delete = connection.createStatement()) {
			delete.execute(deleteSQL.toString());
		}

		StringBuilder insertSQL =
				new StringBuilder(3)
					.append("INSERT INTO ")
					.append(recordType)
					.append("_idx_sort (record_id, indexing_group_id, phrase, ignore_chars_count)" +
							"VALUES (0, 1, ?, 0);");

		try (PreparedStatement insert = connection.prepareStatement(insertSQL.toString())) {
			PreparedStatementUtil.setAllParameters(insert, StringPool.BLANK);

			insert.execute();
		}
	}


	@Override
	public String getVersion() {
		return "4.1.10a";
	}

}

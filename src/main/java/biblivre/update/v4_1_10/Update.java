package biblivre.update.v4_1_10;

import java.sql.Connection;
import java.sql.SQLException;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.bibliographic.indexing_groups.publisher", "Editora");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.bibliographic.indexing_groups.publisher", "Publisher");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.bibliographic.indexing_groups.publisher", "Editora");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.bibliographic.indexing_groups.series", "Série");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.bibliographic.indexing_groups.series", "Series");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.bibliographic.indexing_groups.series", "Serie");
	}

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		_addIndexingGroups(connection);

		_addBriefFormats(connection);

		_updateBriefFormats(connection);

		IndexingGroups.updateIndexingGroup(
				connection, RecordType.BIBLIO, "title",
				"245_a_b,243_a_f,240_a,730_a,740_a_n_p,830_a_v,250_a,130_a");

		IndexingGroups.reset(connection.getSchema(), RecordType.BIBLIO);

		Fields.reset(connection.getSchema(), RecordType.BIBLIO);
	}

	private void _addIndexingGroups(Connection connection) throws SQLException {
		IndexingGroups.addIndexingGroup(connection, RecordType.BIBLIO, "publisher", "260_b", true);

		IndexingGroups.addIndexingGroup(connection, RecordType.BIBLIO, "series", "490_a", true);
	}

	private void _addBriefFormats(Connection connection) throws SQLException {
		for (Object[] entry : _BRIEF_FORMATS_ADDITIONS) {
			String dataField = (String) entry[_DATAFIELD_INDEX];

			String format = (String) entry[_FORMAT_INDEX];

			int sort = (int) entry[_SORT_INDEX];

			Fields.addBriefFormat(connection, RecordType.BIBLIO, dataField, format, sort);
		}
	}

	private void _updateBriefFormats(Connection connection) throws SQLException {
		for (Object[] entry : _BRIEF_FORMATS_UPDATES) {
			String dataField = (String) entry[_DATAFIELD_INDEX];

			String format = (String) entry[_FORMAT_INDEX];

			Fields.updateBriefFormat(connection, RecordType.BIBLIO, dataField, format);
		}
	}

	@Override
	public String getVersion() {
		return "4.1.10";
	}

	private static final int _DATAFIELD_INDEX = 0;

	private static final int _FORMAT_INDEX = 1;

	private static final int _SORT_INDEX = 2;

	private static final Object[][] _BRIEF_FORMATS_UPDATES = new Object[][] {
			{"245", "${a}_{: }${b}_{ / }${c}"},
			{"100", "${a}_{ - }${d}_{ }(${q})"},
			{"110", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})"},
			{"111", "${a}_{. }(${n}_{ : }${d}_{ : }${c})"},
			{"130", "${a}_{. }${l}_{. }${f}"},
			{"600", "${a}_{. }${b}_{. }${c}_{. }${d}_{ - }${x}_{ - }${y}_{ - }${z}"},
			{"610", "${a}_{. }${b}_{ - }${x}_{ - }${y}_{ - }${z}"},
			{"611", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})_{ - }${x}_{ - }${y}_{ - }${z}"},
			{"630", "${a}_{. }(${d})_{ - }${x}_{ - }${y}_{ - }${z}"},
			{"700", "${a}_{. }${d}"},
			{"710", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})"},
			{"711", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})"},
			{"630", "${a}_{. }(${d})"},
		};

	private static final Object[][] _BRIEF_FORMATS_ADDITIONS = new Object[][] {
			{"501", "${a}", 28},
			{"530", "${a}", 31},
			{"595", "${a}", 33}
		};
}

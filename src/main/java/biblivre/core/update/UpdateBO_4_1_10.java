package biblivre.core.update;

import java.sql.SQLException;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;

public class UpdateBO_4_1_10 extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		final String schema = Constants.GLOBAL_SCHEMA;

		Translations.addSingleTranslation(schema, "pt-BR", "cataloging.bibliographic.indexing_groups.publisher", "Editora", 0);
		Translations.addSingleTranslation(schema, "en-US", "cataloging.bibliographic.indexing_groups.publisher", "Publisher", 0);
		Translations.addSingleTranslation(schema, "es", "cataloging.bibliographic.indexing_groups.publisher", "Editora", 0);

		Translations.addSingleTranslation(schema, "pt-BR", "cataloging.bibliographic.indexing_groups.series", "Série", 0);
		Translations.addSingleTranslation(schema, "en-US", "cataloging.bibliographic.indexing_groups.series", "Series", 0);
		Translations.addSingleTranslation(schema, "es", "cataloging.bibliographic.indexing_groups.series", "Serie", 0);
	}

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {

		dao.addIndexingGroup(RecordType.BIBLIO, "publisher", "260_b", true);
		dao.addIndexingGroup(RecordType.BIBLIO, "series", "490_a", true);
		dao.addIndexingGroup(RecordType.BIBLIO, "notes", "500_a,505_a,520_a", true);

		dao.addBriefFormat(RecordType.BIBLIO, "501", "${a}", 28);
		dao.addBriefFormat(RecordType.BIBLIO, "530", "${a}", 31);
		dao.addBriefFormat(RecordType.BIBLIO, "595", "${a}", 33);
		
		dao.updateBriefFormat(RecordType.BIBLIO, "245", "${a}_{: }${b}_{ / }${c}");
		dao.updateBriefFormat(RecordType.BIBLIO, "100", "${a}_{ - }${d}_{ }(${q})");
		dao.updateBriefFormat(RecordType.BIBLIO, "110", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
		dao.updateBriefFormat(RecordType.BIBLIO, "111", "${a}_{. }(${n}_{ : }${d}_{ : }${c})");
		dao.updateBriefFormat(RecordType.BIBLIO, "130", "${a}_{. }${l}_{. }${f}");
		
		dao.updateBriefFormat(RecordType.BIBLIO, "600", "${a}_{. }${b}_{. }${c}_{. }${d}_{ - }${x}_{ - }${y}_{ - }${z}");
		dao.updateBriefFormat(RecordType.BIBLIO, "610", "${a}_{. }${b}_{ - }${x}_{ - }${y}_{ - }${z}");
		dao.updateBriefFormat(RecordType.BIBLIO, "611", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})_{ - }${x}_{ - }${y}_{ - }${z}");
		dao.updateBriefFormat(RecordType.BIBLIO, "630", "${a}_{. }(${d})_{ - }${x}_{ - }${y}_{ - }${z}");
		
		dao.updateBriefFormat(RecordType.BIBLIO, "700", "${a}_{. }${d}");
		dao.updateBriefFormat(RecordType.BIBLIO, "710", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
		dao.updateBriefFormat(RecordType.BIBLIO, "711", "${a}_{. }${b. }_{ }(${n}_{ : }${d}_{ : }${c})");
		dao.updateBriefFormat(RecordType.BIBLIO, "630", "${a}_{. }(${d})");
		
		dao.updateIndexingGroup(RecordType.BIBLIO, "title", "245_a_b,243_a_f,240_a,730_a,740_a_n_p,830_a_v,250_a,130_a");

		Fields.reset(schema, RecordType.BIBLIO);
		IndexingGroups.reset(schema, RecordType.BIBLIO);
	}

	@Override
	protected String getVersion() {
		return "4.1.10";
	}
}

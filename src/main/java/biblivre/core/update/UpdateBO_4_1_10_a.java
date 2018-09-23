package biblivre.core.update;

import java.sql.SQLException;

import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;

public class UpdateBO_4_1_10_a extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		final String schema = Constants.GLOBAL_SCHEMA;
	
		Translations.addSingleTranslation(schema, "pt-BR", "cataloging.tab.record.custom.field_label.biblio_501", "Notas", 0);
		Translations.addSingleTranslation(schema, "en-US", "cataloging.tab.record.custom.field_label.biblio_501", "Notes", 0);
		Translations.addSingleTranslation(schema, "es", "cataloging.tab.record.custom.field_label.biblio_501", "Notas", 0);

		Translations.addSingleTranslation(schema, "pt-BR", "cataloging.tab.record.custom.field_label.biblio_530", "Notas", 0);
		Translations.addSingleTranslation(schema, "en-US", "cataloging.tab.record.custom.field_label.biblio_530", "Notes", 0);
		Translations.addSingleTranslation(schema, "es", "cataloging.tab.record.custom.field_label.biblio_530", "Notas", 0);

		Translations.addSingleTranslation(schema, "pt-BR", "cataloging.tab.record.custom.field_label.biblio_595", "Notas", 0);
		Translations.addSingleTranslation(schema, "en-US", "cataloging.tab.record.custom.field_label.biblio_595", "Notes", 0);
		Translations.addSingleTranslation(schema, "es", "cataloging.tab.record.custom.field_label.biblio_595", "Notas", 0);
	}

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {

		dao.updateBriefFormat(RecordType.BIBLIO, "490", "(${a}_{ ; }${v})");
		dao.updateBriefFormat(RecordType.BIBLIO, "830", "${a}_{. }${p}_{ ; }${v}");

		dao.invalidateIndex(RecordType.BIBLIO);

		Fields.reset(schema, RecordType.BIBLIO);				
	}

	@Override
	protected String getVersion() {
		return "4.1.10a";
	}
}

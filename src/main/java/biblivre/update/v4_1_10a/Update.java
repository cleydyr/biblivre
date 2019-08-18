package biblivre.update.v4_1_10a;

import java.sql.Connection;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addSingleTranslation(
				"pt-BR", "cataloging.tab.record.custom.field_label.biblio_501", "Notas");

		Translations.addSingleTranslation(
				"en-US", "cataloging.tab.record.custom.field_label.biblio_501", "Notes");

		Translations.addSingleTranslation(
				"es", "cataloging.tab.record.custom.field_label.biblio_501", "Notas");

		Translations.addSingleTranslation(
				"pt-BR", "cataloging.tab.record.custom.field_label.biblio_530", "Notas");

		Translations.addSingleTranslation(
				"en-US", "cataloging.tab.record.custom.field_label.biblio_530", "Notes");

		Translations.addSingleTranslation(
				"es", "cataloging.tab.record.custom.field_label.biblio_530", "Notas");

		Translations.addSingleTranslation(
				"pt-BR", "cataloging.tab.record.custom.field_label.biblio_595", "Notas");

		Translations.addSingleTranslation(
				"en-US", "cataloging.tab.record.custom.field_label.biblio_595", "Notes");

		Translations.addSingleTranslation(
				"es", "cataloging.tab.record.custom.field_label.biblio_595", "Notas");
	}

	@Override
	public String getVersion() {
		return "4.1.10a";
	}

}

package biblivre.update.v4_1_10;

import java.sql.Connection;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addSingleTranslation(
				"pt-BR", "cataloging.bibliographic.indexing_groups.publisher", "Editora");

		Translations.addSingleTranslation(
				"en-US", "cataloging.bibliographic.indexing_groups.publisher", "Publisher");

		Translations.addSingleTranslation(
				"es", "cataloging.bibliographic.indexing_groups.publisher", "Editora");

		Translations.addSingleTranslation(
				"pt-BR", "cataloging.bibliographic.indexing_groups.series", "Série");

		Translations.addSingleTranslation(
				"en-US", "cataloging.bibliographic.indexing_groups.series", "Series");

		Translations.addSingleTranslation(
				"es", "cataloging.bibliographic.indexing_groups.series", "Serie");
	}

	@Override
	public String getVersion() {
		return "4.1.10";
	}

}

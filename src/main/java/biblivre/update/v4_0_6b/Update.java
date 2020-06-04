package biblivre.update.v4_0_6b;

import java.sql.Connection;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.reports.title.custom_count",
				"Relatório de contagem pelo campo Marc");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.bibliographic.search.holding_accession_number",
				"Tombo patrimonial");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "cataloging.bibliographic.search.holding_id",
				"Código de barras da etiqueta");

		Translations.addOrReplaceSingleTranslation("pt-BR", "search.holding.shelf_location", "Localização");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "circulation.lending.no_holding_found", "Nenhum exemplar encontrado");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.reports.title.custom_count", "Marc field counting report");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.bibliographic.search.holding_accession_number",
				"Asset number");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "cataloging.bibliographic.search.holding_id",
				"Label barcode number");

		Translations.addOrReplaceSingleTranslation("en-US", "search.holding.shelf_location",	"Location");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "circulation.lending.no_holding_found", "No copy found");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.reports.title.custom_count",
				"Informe de recuento del campo Marc");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.bibliographic.search.holding_accession_number",
				"Sello patrimonial");

		Translations.addOrReplaceSingleTranslation(
				"es", "cataloging.bibliographic.search.holding_id",
				"Código de barras de la etiqueta");

		Translations.addOrReplaceSingleTranslation("es", "search.holding.shelf_location", "Localización");

		Translations.addOrReplaceSingleTranslation(
				"es", "circulation.lending.no_holding_found",
				"Ningún ejemplar encontrado");
	}

	@Override
	public String getVersion() {
		return "4.0.6b";
	}

}

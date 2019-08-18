package biblivre.update.v4_0_6b;

import java.sql.Connection;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addSingleTranslation(
				"pt-BR", "administration.reports.title.custom_count",
				"Relatório de contagem pelo campo Marc");

		Translations.addSingleTranslation(
				"pt-BR", "cataloging.bibliographic.search.holding_accession_number",
				"Tombo patrimonial");

		Translations.addSingleTranslation(
				"pt-BR", "cataloging.bibliographic.search.holding_id",
				"Código de barras da etiqueta");

		Translations.addSingleTranslation("pt-BR", "search.holding.shelf_location", "Localização");

		Translations.addSingleTranslation(
				"pt-BR", "circulation.lending.no_holding_found", "Nenhum exemplar encontrado");

		Translations.addSingleTranslation(
				"en-US", "administration.reports.title.custom_count", "Marc field counting report");

		Translations.addSingleTranslation(
				"en-US", "cataloging.bibliographic.search.holding_accession_number",
				"Asset number");

		Translations.addSingleTranslation(
				"en-US", "cataloging.bibliographic.search.holding_id",
				"Label barcode number");

		Translations.addSingleTranslation("en-US", "search.holding.shelf_location",	"Location");

		Translations.addSingleTranslation(
				"en-US", "circulation.lending.no_holding_found", "No copy found");

		Translations.addSingleTranslation(
				"es", "administration.reports.title.custom_count",
				"Informe de recuento del campo Marc");

		Translations.addSingleTranslation(
				"es", "cataloging.bibliographic.search.holding_accession_number",
				"Sello patrimonial");

		Translations.addSingleTranslation(
				"es", "cataloging.bibliographic.search.holding_id",
				"Código de barras de la etiqueta");

		Translations.addSingleTranslation("es", "search.holding.shelf_location", "Localización");

		Translations.addSingleTranslation(
				"es", "circulation.lending.no_holding_found",
				"Ningún ejemplar encontrado");
	}

	@Override
	public String getVersion() {
		return "4.0.6b";
	}

}

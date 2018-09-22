package biblivre.core.update;

import biblivre.core.translations.Translations;

public class UpdateBO_4_0_6_b extends UpdateBO {

	@Override
	protected void doGlobalUpdate() {
		Translations.addSingleTranslation("global", "pt-BR", "administration.reports.title.custom_count", "Relatório de contagem pelo campo Marc", 0);
		Translations.addSingleTranslation("global", "pt-BR", "cataloging.bibliographic.search.holding_accession_number", "Tombo patrimonial", 0);
		Translations.addSingleTranslation("global", "pt-BR", "cataloging.bibliographic.search.holding_id", "Código de barras da etiqueta", 0);
		Translations.addSingleTranslation("global", "pt-BR", "search.holding.shelf_location", "Localização", 0);
		Translations.addSingleTranslation("global", "pt-BR", "circulation.lending.no_holding_found", "Nenhum exemplar encontrado", 0);

		Translations.addSingleTranslation("global", "en-US", "administration.reports.title.custom_count", "Marc field counting report", 0);
		Translations.addSingleTranslation("global", "en-US", "cataloging.bibliographic.search.holding_accession_number", "Asset number", 0);
		Translations.addSingleTranslation("global", "en-US", "cataloging.bibliographic.search.holding_id", "Label barcode number", 0);
		Translations.addSingleTranslation("global", "en-US", "search.holding.shelf_location", "Location", 0);
		Translations.addSingleTranslation("global", "en-US", "circulation.lending.no_holding_found", "No copy found", 0);

		Translations.addSingleTranslation("global", "es", "administration.reports.title.custom_count", "Informe de recuento del campo Marc", 0);
		Translations.addSingleTranslation("global", "es", "cataloging.bibliographic.search.holding_accession_number", "Sello patrimonial", 0);
		Translations.addSingleTranslation("global", "es", "cataloging.bibliographic.search.holding_id", "Código de barras de la etiqueta", 0);
		Translations.addSingleTranslation("global", "es", "search.holding.shelf_location", "Localización", 0);
		Translations.addSingleTranslation("global", "es", "circulation.lending.no_holding_found", "Ningún ejemplar encontrado", 0);
	}

	@Override
	protected String getVersion() {
		return "4.0.6b";
	}
}

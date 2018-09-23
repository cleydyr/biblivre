package biblivre.core.update;

import biblivre.core.translations.Translations;

public class UpdateBO_4_0_8_b extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) {
		Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre3restore.log_header", "[Log de restauração de backup do Biblivre 3]\n\n", 0);

		Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre3restore.log_header", "[Log for Biblivre 3 backup restoration]\n\n", 0);

		Translations.addSingleTranslation("global", "es", "administration.setup.biblivre3restore.log_header", "[Log de restauración de backup del Biblivre 3]\n\n", 0);
	}

	@Override
	protected String getVersion() {
		return "4.0.8b";
	}
}

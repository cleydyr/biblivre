package biblivre.update.v4_0_12b;

import java.sql.Connection;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addSingleTranslation(
				"global", "pt-BR", "administration.setup.biblivre4restore.skip", "Ignorar", 0);

		Translations.addSingleTranslation(
				"global", "en-US", "administration.setup.biblivre4restore.skip", "Skip", 0);

		Translations.addSingleTranslation(
				"global", "es", "administration.setup.biblivre4restore.skip", "Pasar", 0);

		Translations.addSingleTranslation(
				"global", "pt-BR",
				"administration.setup.biblivre4restore.error.digital_media_only_selected",
				"O Backup selecionado contém apenas arquivos digitais. Tente novamente usando " +
						"um backup completo ou parcial sem arquivos digitais", 0);

		Translations.addSingleTranslation(
				"global", "en-US",
				"administration.setup.biblivre4restore.error.digital_media_only_selected",
				"The selected Backup is a Digital Media Only file.  Try again using a Complete " +
						"Backup file or one without Digital Media", 0);

		Translations.addSingleTranslation(
				"global", "es",
				"administration.setup.biblivre4restore.error.digital_media_only_selected",
				"La copia de seguridad seleccionada contiene sólo los archivos digitales. Trate " +
						"de usar una copia de seguridad completa o parcial sin archivos digitales",
				0);

		Translations.addSingleTranslation(
				"global", "pt-BR",
				"administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
				"O segundo arquivo de backup selecionado não contém apenas arquivos digitais", 0);

		Translations.addSingleTranslation(
				"global", "en-US",
				"administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
				"The second file selected is not a Digital Media Only file", 0);

		Translations.addSingleTranslation(
				"global", "es",
				"administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
				"El segundo archivo que seleccionó no  contiene sólo archivos digitales", 0);

		Translations.addSingleTranslation(
				"global", "pt-BR",
				"administration.setup.biblivre4restore.select_digital_media",
				"Selecione um Backup de Mídias Digitais", 0);

		Translations.addSingleTranslation(
				"global", "en-US", "administration.setup.biblivre4restore.select_digital_media",
				"Select a Digital Media Backup file", 0);

		Translations.addSingleTranslation(
				"global", "es", "administration.setup.biblivre4restore.select_digital_media",
				"Seleccione una copia de seguridad de archivos digitales", 0);

		Translations.addSingleTranslation(
				"global", "pt-BR",
				"administration.setup.biblivre4restore.select_digital_media.description",
				"O arquivo de backup selecionado anteriormente não possui Mídias Digitais.  " + 
						"Caso você possua um backup somente de Mídias Digitais, selecione " +
						"abaixo o arquivo desejado, ou faça o upload do mesmo. Caso não " +
						"deseje importar Mídias Digitais, clique no botão " +
						"<strong>Ignorar</strong>.", 0);

		Translations.addSingleTranslation(
				"global", "en-US",
				"administration.setup.biblivre4restore.select_digital_media.description",
				"The previously selected Backup file doesn't have any Digital Media. If you " + 
					"have a Digital Media Only backup, select the desired one below, or upload " + 
					"the Digital Media Only backup file. If you don't want to import Digital " + 
					"Media, click on <strong>Skip</strong>.", 0);

		Translations.addSingleTranslation(
				"global", "es",
				"administration.setup.biblivre4restore.select_digital_media.description",
				"El archivo de copia de seguridad seleccionado previamente no contiene archivos " +
						"digitales. Si usted tiene una copia de seguridad de sólo archivos " +
						"digitales, seleccione el archivo que desee a continuación, o cargar " +
						"el mismo. Si no desea importar Digital Media, haga clic en " + 
						"<strong>Pasar</ strong>.",	0);

		Translations.addSingleTranslation(
				"global", "pt-BR", "multi_schema.manage.drop_schema.confirm_title",
				"Excluir biblioteca", 0);

		Translations.addSingleTranslation(
				"global", "en-US", "multi_schema.manage.drop_schema.confirm_title",
				"Delete library", 0);

		Translations.addSingleTranslation(
				"global", "es", "multi_schema.manage.drop_schema.confirm_title",
				"Excluir biblioteca", 0);

		Translations.addSingleTranslation(
				"global", "pt-BR", "multi_schema.manage.drop_schema.confirm_description",
				"Você realmente deseja excluir esta biblioteca?", 0);

		Translations.addSingleTranslation(
				"global", "en-US", "multi_schema.manage.drop_schema.confirm_description",
				"Do you really want to delete this library?", 0);

		Translations.addSingleTranslation(
				"global", "es", "multi_schema.manage.drop_schema.confirm_description",
				"¿Usted realmente desea excluir esta biblioteca?", 0);

		Translations.addSingleTranslation(
				"global", "pt-BR", "multi_schema.manage.drop_schema.confirm",
				"Ela será excluída permanentemente do sistema e não poderá ser recuperada", 0);

		Translations.addSingleTranslation(
				"global", "en-US", "multi_schema.manage.drop_schema.confirm",
				"It will be deleted from the system forever and cannot be restored", 0);

		Translations.addSingleTranslation(
				"global", "es", "multi_schema.manage.drop_schema.confirm",
				"La biblioteca será excluida permanentemente del sistema y no podrá ser recuperada",
				0);
	}

	@Override
	public String getVersion() {
		return "4.0.12b";
	}

}

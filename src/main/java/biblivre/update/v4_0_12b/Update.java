package biblivre.update.v4_0_12b;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) {
		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.setup.biblivre4restore.skip", "Ignorar");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.setup.biblivre4restore.skip", "Skip");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.setup.biblivre4restore.skip", "Pasar");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.setup.biblivre4restore.error.digital_media_only_selected",
				"O Backup selecionado contém apenas arquivos digitais. Tente novamente usando " +
						"um backup completo ou parcial sem arquivos digitais");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.setup.biblivre4restore.error.digital_media_only_selected",
				"The selected Backup is a Digital Media Only file.  Try again using a Complete " +
						"backup file or one without Digital Media");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.setup.biblivre4restore.error.digital_media_only_selected",
				"La copia de seguridad seleccionada contiene sólo los archivos digitales. Trate " +
						"de usar una copia de seguridad completa o parcial sin archivos digitales");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR",
				"administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
				"O segundo arquivo de backup selecionado não contém apenas arquivos digitais");

		Translations.addOrReplaceSingleTranslation(
				"en-US",
				"administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
				"The second file selected is not a Digital Media Only file");

		Translations.addOrReplaceSingleTranslation(
				"es",
				"administration.setup.biblivre4restore.error.digital_media_only_should_be_selected",
				"El segundo archivo que seleccionó no  contiene sólo archivos digitales");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.setup.biblivre4restore.select_digital_media",
				"Selecione um Backup de Mídias Digitais");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.setup.biblivre4restore.select_digital_media",
				"Select a Digital Media Backup file");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.setup.biblivre4restore.select_digital_media",
				"Seleccione una copia de seguridad de archivos digitales");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR",  "administration.setup.biblivre4restore.select_digital_media.description",
				"O arquivo de backup selecionado anteriormente não possui Mídias Digitais.  " +
						"Caso você possua um backup somente de Mídias Digitais, selecione " +
						"abaixo o arquivo desejado, ou faça o upload do mesmo. Caso não " +
						"deseje importar Mídias Digitais, clique no botão " +
						"<strong>Ignorar</strong>.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.setup.biblivre4restore.select_digital_media.description",
				"The previously selected Backup file doesn't have any Digital Media. If you " +
					"have a Digital Media Only backup, select the desired one below, or upload " +
					"the Digital Media Only backup file. If you don't want to import Digital " +
					"Media, click on <strong>Skip</strong>.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.setup.biblivre4restore.select_digital_media.description",
				"El archivo de copia de seguridad seleccionado previamente no contiene archivos " +
						"digitales. Si usted tiene una copia de seguridad de sólo archivos " +
						"digitales, seleccione el archivo que desee a continuación, o cargar " +
						"el mismo. Si no desea importar Digital Media, haga clic en " +
						"<strong>Pasar</ strong>.");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "multi_schema.manage.drop_schema.confirm_title", "Excluir biblioteca");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "multi_schema.manage.drop_schema.confirm_title", "Delete library");

		Translations.addOrReplaceSingleTranslation(
				"es", "multi_schema.manage.drop_schema.confirm_title", "Excluir biblioteca");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "multi_schema.manage.drop_schema.confirm_description",
				"Você realmente deseja excluir esta biblioteca?");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "multi_schema.manage.drop_schema.confirm_description",
				"Do you really want to delete this library?");

		Translations.addOrReplaceSingleTranslation(
				"es", "multi_schema.manage.drop_schema.confirm_description",
				"¿Usted realmente desea excluir esta biblioteca?");

		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "multi_schema.manage.drop_schema.confirm",
				"Ela será excluída permanentemente do sistema e não poderá ser recuperada");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "multi_schema.manage.drop_schema.confirm",
				"It will be deleted from the system forever and cannot be restored");

		Translations.addOrReplaceSingleTranslation(
				"es", "multi_schema.manage.drop_schema.confirm",
				"La biblioteca será excluida permanentemente del sistema y no podrá ser " +
						"recuperada");
	}

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		_fixAuthoritiesAutoComplete(connection);

		_fixVocabularyAutoComplete(connection);
	}


	private void _fixAuthoritiesAutoComplete(Connection connection) throws SQLException {
		String sql = "UPDATE biblio_form_subfields SET autocomplete_type = 'authorities' " +
				"WHERE subfield = 'a' AND datafield in ('100', '110', '111');";

		try (Statement st = connection.createStatement()) {
			st.execute(sql);
		};
	}

	private void _fixVocabularyAutoComplete(Connection connection) throws SQLException {
		String sql = "UPDATE biblio_form_subfields SET autocomplete_type = 'vocabulary' " +
				"WHERE subfield = 'a' AND datafield in ('600', '610', '611', '630', '650', '651');";

		try (Statement st = connection.createStatement()) {
			st.execute(sql);
		}
	}

	@Override
	public String getVersion() {
		return "4.0.12b";
	}

}

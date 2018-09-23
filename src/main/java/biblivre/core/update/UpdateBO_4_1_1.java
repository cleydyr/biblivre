package biblivre.core.update;

import java.sql.SQLException;

import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;

public class UpdateBO_4_1_1 extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		final String schema = Constants.GLOBAL_SCHEMA;

		Translations.addSingleTranslation(schema, "pt-BR", "multi_schema.backup.display_and_select_libraries", "Exibir e selecionar bibliotecas {min} a {max}", 0);
		Translations.addSingleTranslation(schema, "en-US", "multi_schema.backup.display_and_select_libraries", "Show and select libraries from {min} to {max}", 0);
		Translations.addSingleTranslation(schema, "es", "multi_schema.backup.display_and_select_libraries", "Ver y seleccionar las bibliotecas de {min} a {max}", 0);
		
		Translations.addSingleTranslation(schema, "pt-BR", "administration.setup.biblivre4restore.select_file", "Selecione um arquivo de backup do Biblivre 4", 0);
		Translations.addSingleTranslation(schema, "en-US", "administration.setup.biblivre4restore.select_file", "Select a Biblivre 4 backup file", 0);
		Translations.addSingleTranslation(schema, "es", "administration.setup.biblivre4restore.select_file", "Seleccione un archivo de copia de seguridad Biblivre 4", 0);

		Translations.addSingleTranslation(schema, "pt-BR", "multi_schema.restore.limit.title", "Bibliotecas no arquivo selecionado", 0);
		Translations.addSingleTranslation(schema, "en-US", "multi_schema.restore.limit.title", "Libraries in the selected file", 0);
		Translations.addSingleTranslation(schema, "es", "multi_schema.restore.limit.title", "Bibliotecas en el archivo seleccionado", 0);

		Translations.addSingleTranslation(schema, "pt-BR", "multi_schema.restore.limit.description", "O arquivo selecionado possui um número muito grande de bibliotecas. Por limites do banco de dados, a restauração deverá ser feita em passos, de no máximo 20 bibliotecas por passo. Clique nos links abaixo para listar as bibliotecas desejadas, e selecione as bibliotecas que serão restauradas. Repita esse procedimento até que todas as bibliotecas desejadas tenham sido restauradas.", 0);
		Translations.addSingleTranslation(schema, "en-US", "multi_schema.restore.limit.description", "The selected file contains a high number of libraries. Due to database limitations, you should restore those libraries in steps, limited to 20 libraries in each step. Click in a link below to list the desired libraries, and select the ones you want to restore. Repeat these steps untill you've restored all the libraries you need.", 0);
		Translations.addSingleTranslation(schema, "es", "multi_schema.restore.limit.description", " El archivo seleccionado tiene un gran número de bibliotecas. Debido a las limitaciones de la base de datos, la restauración debe hacerse en pasos de hasta 20 bibliotecas a paso. Haga clic en los enlaces abajo para enumerar la biblioteca que desee y seleccione las bibliotecas que se restaurarán. Repita este procedimiento hasta que se hayan restaurado todas las bibliotecas deseadas.", 0);
	}

	@Override
	protected String getVersion() {
		return "4.1.1";
	}
}

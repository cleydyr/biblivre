package biblivre.core.update;

import java.sql.SQLException;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.configurations.Configurations;
import biblivre.core.translations.Translations;

public class UpdateBO_5_0_1_b extends UpdateBO {

	@Override
	protected void doGlobalUpdate(UpdatesDAO dao) throws SQLException {
		Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore.description", "Use esta opção caso você queira restaurar um backup existente do Biblivre 4. Caso o Biblivre encontre backups salvos em seus documentos, você poderá restaurá-los diretamente da lista abaixo. Caso contrário, você deverá enviar um arquivo de backup (extensão <strong>.b4bz</strong> ou <strong>.b5bz</strong>) através do formulário.", 0);
		Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore.description", "Use this option should you wish to restore an existing Biblivre 4 backup. Should Biblivre find backups saved in your documents, you will be able to restore them directly from the list below. Otherwise, you will have to send a backup file (extension <strong>.b4bz</strong> or <strong>.b5bz</strong>) by means of the form.", 0);
		Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore.description", "administration.setup.biblivre4restore.description', 'Use esta opción en caso que usted quiera restaurar un backup existente del Biblivre 4. En caso que el Biblivre encuentre backups guardados en sus documentos, usted podrá restaurarlos directamente de la lista abajo. En caso contrario, usted deberá enviar un archivo de backup (extensión <strong>.b4bz</strong> o <strong>.b5bz</strong>) a través del formulario.", 0);
		
		Translations.addSingleTranslation("global", "pt-BR", "multi_schema.select_restore.description", "Use esta opção caso você queira restaurar um backup existente do Biblivre 4. Caso o Biblivre encontre backups salvos em seus documentos, você poderá restaurá-los diretamente da lista abaixo. Caso contrário, você deverá enviar um arquivo de backup (extensão <strong>.b4bz</strong>) através do formulário.", 0);
		Translations.addSingleTranslation("global", "en-US", "multi_schema.select_restore.description", "Use this option if you wish to restore an existing Biblivre 4 backup. When the Biblivre find backups saved among your documents, you will be able to restore the, directly from the list below. Otherwise, you will have to send a backup file (extension <strong>.b4bz</strong>) through the form.", 0);
		Translations.addSingleTranslation("global", "es", "multi_schema.select_restore.description", "Use esta opción en caso de desear restaurar un backup existente del Biblivre 4. En el caso de que el Biblivre encuentre backups guardados en sus documentos, usted podrá restaurarlos directamente de la lista siguiente. De lo contrario, usted deberá enviar un archivo de backup (extensión <strong>.b4bz</strong>) a través del formulario.", 0);
		
		Translations.addSingleTranslation("global", "pt-BR", "administration.setup.biblivre4restore", "Restaurar um Backup do Biblivre 4 ou Biblivre 5", 0);
		Translations.addSingleTranslation("global", "en-US", "administration.setup.biblivre4restore", "Restore a Biblivre 4 or Biblivre 5 Backup", 0);
		Translations.addSingleTranslation("global", "es", "administration.setup.biblivre4restore", "Restaurar un Backup del Biblivre 4 o Biblivre 5", 0);
		 
		Translations.reset();
		Configurations.reset();
	}

	@Override
	protected void doSchemaUpdate(UpdatesDAO dao, String schema)
			throws SQLException {

		dao.addBriefFormatSortOrderColumns(RecordType.BIBLIO);
		dao.addBriefFormatSortOrderColumns(RecordType.AUTHORITIES);
		dao.addBriefFormatSortOrderColumns(RecordType.VOCABULARY);
	}
	@Override
	protected String getVersion() {
		return "5.0.1";
	}
}

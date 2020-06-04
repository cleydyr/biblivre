package biblivre.update.v5_0_1b;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.cataloging.enums.RecordType;
import biblivre.core.configurations.Configurations;
import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdate(Connection connection) throws SQLException {
		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.setup.biblivre4restore.description",
				"Use esta opção caso você queira restaurar um backup existente do Biblivre 4. " +
					"Caso o Biblivre encontre backups salvos em seus documentos, você poderá " +
					"restaurá-los diretamente da lista abaixo. Caso contrário, você deverá " +
					"enviar um arquivo de backup (extensão <strong>.b4bz</strong> ou <strong>" +
					".b5bz</strong>) através do formulário.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.setup.biblivre4restore.description",
				"Use this option should you wish to restore an existing Biblivre 4 backup. " +
					"Should Biblivre find backups saved in your documents, you will be able to " +
					"restore them directly from the list below. Otherwise, you will have to send " +
					"a backup file (extension <strong>.b4bz</strong> or <strong>.b5bz</strong>) " +
					"by means of the form.");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.setup.biblivre4restore.description",
				"Use esta opción en caso que usted quiera restaurar un backup existente del " +
				"Biblivre 4. En caso que el Biblivre encuentre backups guardados en sus " +
					"documentos, usted podrá restaurarlos directamente de la lista abajo. En " +
					"caso contrario, usted deberá enviar un archivo de backup (extensión " +
					"<strong>.b4bz</strong> o <strong>.b5bz</strong>) a través del formulario.");


		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "multi_schema.select_restore.description",
				"Use esta opção caso você queira restaurar um backup existente do Biblivre 4. " +
					"Caso o Biblivre encontre backups salvos em seus documentos, você poderá " +
					"restaurá-los diretamente da lista abaixo. Caso contrário, você deverá " +
					"enviar um arquivo de backup (extensão <strong>.b4bz</strong>) através do " +
					"formulário.");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "multi_schema.select_restore.description",
				"Use this option if you wish to restore an existing Biblivre 4 backup. When the " +
					"Biblivre find backups saved among your documents, you will be able to " +
					"restore the, directly from the list below. Otherwise, you will have to send " +
					"a backup file (extension <strong>.b4bz</strong>) through the form.");

		Translations.addOrReplaceSingleTranslation(
				"es", "multi_schema.select_restore.description",
				"Use esta opción en caso de desear restaurar un backup existente del Biblivre 4. " +
					"En el caso de que el Biblivre encuentre backups guardados en sus " +
					"documentos, usted podrá restaurarlos directamente de la lista siguiente. " +
					"De lo contrario, usted deberá enviar un archivo de backup (extensión " +
					"<strong>.b4bz</strong>) a través del formulario.");


		Translations.addOrReplaceSingleTranslation(
				"pt-BR", "administration.setup.biblivre4restore",
				"Restaurar um Backup do Biblivre 4 ou Biblivre 5");

		Translations.addOrReplaceSingleTranslation(
				"en-US", "administration.setup.biblivre4restore",
				"Restore a Biblivre 4 or Biblivre 5 Backup");

		Translations.addOrReplaceSingleTranslation(
				"es", "administration.setup.biblivre4restore",
				"Restaurar un Backup del Biblivre 4 o Biblivre 5");

	}

	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		for (RecordType recordType :
			new RecordType[] {RecordType.BIBLIO, RecordType.AUTHORITIES,
					RecordType.VOCABULARY}) {

			_addBriefFormatSortOrderColumns(connection, recordType);
		}
	}

	public void _addBriefFormatSortOrderColumns(Connection connection, RecordType recordType)
		throws SQLException {

		String tableName = recordType + "_brief_formats";

		if (UpdateService.checkColumnExistence(tableName, "sort_order", connection)) {
			return;
		}

		_addSortOrderColumnForTable(tableName, connection);

		_updateSortOrderForTable(tableName, connection);
	}

	@Override
	public void afterUpdate() {
		Translations.reset();

		Configurations.reset();

	}

	@Override
	public String getVersion() {
		return "5.0.1b";
	}

	private void _addSortOrderColumnForTable(String tableName, Connection con) throws SQLException {
		StringBuilder addSortOrderColumnSQL =
				new StringBuilder(3)
					.append("ALTER TABLE ")
					.append(tableName)
					.append(" ADD COLUMN sort_order integer;");

		try (Statement addDatafieldColumnSt = con.createStatement()) {
			addDatafieldColumnSt.execute(addSortOrderColumnSQL.toString());
		}
	}

	private void _updateSortOrderForTable(String tableName, Connection con) throws SQLException {
		StringBuilder updateSql =
				new StringBuilder(3)
					.append("UPDATE ")
					.append(tableName)
					.append(" SET sort_order = (CAST(datafield as INT));");

		try (Statement updateSt = con.createStatement()) {
			updateSt.execute(updateSql.toString());
		}
	}
}

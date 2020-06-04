package biblivre.update.v5_1_23;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	public void doUpdate(Connection connection) throws SQLException {
		_addTranslations();
		_addConfiguration(connection);
	}

	@Override
	public String getVersion() {
		return "6.0.0-1.0.1-alpha";
	}

	private void _addTranslations() throws SQLException {
		for (Map.Entry<String, Map<String, String>> entry: _TRANSLATIONS.entrySet()) {
			for (Map.Entry<String, String> entry2: entry.getValue().entrySet()) {
				String key = entry.getKey();

				String language = entry2.getKey();

				String translation = entry2.getValue();

				Translations.addOrReplaceSingleTranslation(language, key, translation);
			}
		}
	}

	private void _addConfiguration(Connection connection) {
		try (PreparedStatement statement = connection.prepareStatement(INSERT_CONFIG_SQL)) {
			statement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String INSERT_CONFIG_SQL =
			"INSERT INTO configurations (key, value, type, required, modified, modified_by) "
					+ "VALUES ('holding.label_print_paragraph_alignment', 'ALIGN_CENTER', 'string',"
					+ "true, '2014-06-21 11:42:07.150326', 1);";

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private static final Map<String, Map<String, String>> _TRANSLATIONS = new HashMap() {{
		put("administration.configuration.title.holding.label_print_paragraph_alignment", new HashMap() {{
			put("pt-BR", "Alinhamento de parágrafo");
			put("es", "Alineación de párrafo");
			put("en-US", "Paragraph alignment");
		}});

		put("administration.configuration.description.holding.label_print_paragraph_alignment", new HashMap() {{
			put("pt-BR", "Alinhamento de parágrafo que será utilizado em cada etiqueta impressa");
			put("es", "Alineación de párrafo que va a ser usado en cada etiqueta impresa");
			put("en-US", "Paragraph alignment which will be used in each printed label");
		}});

		put("administration.configuration.label_print.ALIGN_CENTER", new HashMap() {{
			put("pt-BR", "Centralizado");
			put("es", "Centrado");
			put("en-US", "Center");
		}});

		put("administration.configuration.label_print.ALIGN_JUSTIFIED_ALL", new HashMap() {{
			put("pt-BR", "Justificado (tudo)");
			put("es", "Justificado (todo)");
			put("en-US", "Justified (all)");
		}});

		put("administration.configuration.label_print.ALIGN_JUSTIFIED", new HashMap() {{
			put("pt-BR", "Justificado");
			put("es", "Justificado");
			put("en-US", "Justified");
		}});

		put("administration.configuration.label_print.ALIGN_LEFT", new HashMap() {{
			put("pt-BR", "À esquerda");
			put("es", "A la izquierda");
			put("en-US", "Left");
		}});

		put("administration.configuration.label_print.ALIGN_RIGHT", new HashMap() {{
			put("pt-BR", "À direita");
			put("es", "A la derecha");
			put("en-US", "Right");
		}});
	}};
}

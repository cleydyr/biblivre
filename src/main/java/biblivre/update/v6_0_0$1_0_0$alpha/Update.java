package biblivre.update.v6_0_0$1_0_0$alpha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	public void doUpdate(Connection connection) throws SQLException {
		_addTranslations(connection);
	}

	@Override
	public String getVersion() {
		return "6.0.0-1.0.0-alpha";
	}

	private void _addTranslations(Connection connection) throws SQLException {
		for (Map.Entry<String, Map<String, String>> entry: _TRANSLATIONS.entrySet()) {
			for (Map.Entry<String, String> entry2: entry.getValue().entrySet()) {
				String key = entry.getKey();

				String language = entry2.getKey();

				String translation = entry2.getValue();

				Translations.addSingleTranslation(language, key, translation);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private Map<String, Map<String, String>> _TRANSLATIONS = new HashMap() {{
		put("cataloging.reservation.error.limit_exceeded", new HashMap() {{
			put("en-US", "The selected reader surpassed the limit of authorized loans");
			put("es", "El lector seleccionado excedió el límite de reservas permitidas");
			put("pt-BR", "O leitor selecionado ultrapassou o limite de reservas permitidas");
		}});

		put("cataloging.import.error.file_upload_error", new HashMap() {{
			put("en-US", "Couldn't upload file. Please contact the administrator to analyze this" +
					"problem.");
			put("es", "No ha sido posible subir el archivo. Por favor, contacta el administrador " +
					"del sistema para analizar este problema.");
			put("pt-BR", "Não foi possível fazer upload do arquivo. Por favor, contacte o " +
					"administrador do sistema para anlizar este problema.");
		}});
	}};

}

package biblivre.update.v5_1_30;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;

public class Update implements UpdateService {

	public void doUpdate(Connection connection) throws SQLException {
		_addTranslations();
	}

	@Override
	public String getVersion() {
		return "5.1.30";
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

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private static final Map<String, Map<String, String>> _TRANSLATIONS = new HashMap() {{
		put("administration.migration.groups.users", new HashMap() {{
			put("pt-BR", "Usuários, logins de acesso, permissões e tipos de usuários");
			put("es", "Usuarios, logins de acceso, permisos y tipos de usuarios");
			put("en-US", "Users, access logins, permissions and user types");
		}});
	}};
}

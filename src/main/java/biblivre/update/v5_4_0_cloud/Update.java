package biblivre.update.v5_4_0_cloud;

import biblivre.core.translations.Translations;
import biblivre.update.UpdateService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Update implements UpdateService {

    @Override
    public void doUpdateScopedBySchema(Connection connection) throws SQLException {
        _updateBiblioFormDataFields(connection);
        _addTranslations(connection.getSchema());
    }

    private void _updateBiblioFormDataFields(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "UPDATE "
                            + connection.getSchema()
                            + ".biblio_form_datafields "
                            + "set material_type = concat('legal_stance,', material_type) "
                            + "where material_type like '%thesis%' AND material_type NOT LIKE '%legal_stance%'");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void _addTranslations(String schema) throws SQLException {
        for (Map.Entry<String, Map<String, String>> entry : _TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                String key = entry.getKey();

                String language = entry2.getKey();

                String translation = entry2.getValue();

                Translations.addSingleTranslation(schema, language, key, translation);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes", "serial"})
    private static final Map<String, Map<String, String>> _TRANSLATIONS =
            new HashMap() {
                {
                    put(
                            "marc.material_type.legal_stance",
                            new HashMap() {
                                {
                                    put("pt-BR", "Parecer legal");
                                    put("es", "Parecer legal");
                                    put("en-US", "Legal stance");
                                }
                            });
                }
            };

    @Override
    public String getVersion() {
        return "5.4.0";
    }
}

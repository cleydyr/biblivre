package biblivre.update.v6_0_0$1_0_1$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    private TranslationBO translationBO;

    public void doUpdate(Connection connection) throws UpdateException {
        _addTranslations();
        _addConfiguration(connection);
    }

    private void _addTranslations() {
        for (Map.Entry<String, Map<String, String>> entry : _TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                String key = entry.getKey();

                String language = entry2.getKey();

                String translation = entry2.getValue();

                translationBO.addSingleTranslation(language, key, translation);
            }
        }
    }

    private void _addConfiguration(Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_CONFIG_SQL)) {
            statement.execute();
        } catch (SQLException sqlException) {
            throw new UpdateException("can't add configuration", sqlException);
        }
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    private static final String INSERT_CONFIG_SQL =
            "INSERT INTO configurations (key, value, type, required, modified, modified_by) "
                    + "VALUES ('holding.label_print_paragraph_alignment', 'ALIGN_CENTER', 'string',"
                    + "true, '2014-06-21 11:42:07.150326', 1) ON CONFLICT DO NOTHING;";

    private static final Map<String, Map<String, String>> _TRANSLATIONS =
            Map.of(
                    "administration.configuration.title.holding.label_print_paragraph_alignment",
                    Map.of(
                            "pt-BR",
                            "Alinhamento de parágrafo",
                            "es",
                            "Alineación de párrafo",
                            "en-US",
                            "Paragraph alignment"),
                    "administration.configuration.description.holding.label_print_paragraph_alignment",
                    Map.of(
                            "pt-BR",
                            "Alinhamento de parágrafo que será utilizado em cada etiqueta impressa",
                            "es",
                            "Alineación de párrafo que va a ser usado en cada etiqueta impresa",
                            "en-US",
                            "Paragraph alignment which will be used in each printed label"),
                    "administration.configuration.label_print.ALIGN_CENTER",
                    Map.of("pt-BR", "Centralizado", "es", "Centrado", "en-US", "Center"),
                    "administration.configuration.label_print.ALIGN_JUSTIFIED_ALL",
                    Map.of(
                            "pt-BR", "Justificado (tudo)",
                            "es", "Justificado (todo)",
                            "en-US", "Justified (all)"),
                    "administration.configuration.label_print.ALIGN_JUSTIFIED",
                    Map.of(
                            "pt-BR", "Justificado",
                            "es", "Justificado",
                            "en-US", "Justified"),
                    "administration.configuration.label_print.ALIGN_LEFT",
                    Map.of(
                            "pt-BR", "À esquerda",
                            "es", "A la izquierda",
                            "en-US", "Left"),
                    "administration.configuration.label_print.ALIGN_RIGHT",
                    Map.of("pt-BR", "À direita", "es", "A la derecha", "en-US", "Right"));
}

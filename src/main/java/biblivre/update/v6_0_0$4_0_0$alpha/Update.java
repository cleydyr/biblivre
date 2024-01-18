package biblivre.update.v6_0_0$3_0_1$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    private TranslationBO translationBO;

    public void doUpdate(Connection connection) throws UpdateException {
        _addTranslations(connection);
    }

    private void _addTranslations(Connection connection) throws UpdateException {
        for (Map.Entry<String, Map<String, String>> entry : _TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                String key = entry.getKey();

                String language = entry2.getKey();

                String translation = entry2.getValue();

                translationBO.addSingleTranslation(language, key, translation);
            }
        }
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    private final Map<String, Map<String, String>> _TRANSLATIONS =
            Map.of(
                    "error.cannot_create_file",
                    Map.of(
                            "en-US",
                            "Cannot create a temporary file",
                            "es",
                            "No se ha podido crear un archivo temporario",
                            "pt-BR",
                            "Não foi possível criar um arquivo temporário"));
}

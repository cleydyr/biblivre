package biblivre.update.v6_0_0$1_0_2$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("6.0.0-1.0.2-alpha")
public class Update implements UpdateService {

    private TranslationBO translationBO;

    public void doUpdate(Connection connection) throws SQLException {
        _addTranslations();
    }

    private void _addTranslations() throws SQLException {
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

    @SuppressWarnings({"unchecked", "rawtypes", "serial"})
    private static final Map<String, Map<String, String>> _TRANSLATIONS =
            new HashMap() {
                {
                    put(
                            "circulation.error.invalid_user_name",
                            new HashMap() {
                                {
                                    put(
                                            "pt-BR",
                                            "Este usuário possui nome com caracteres inválidos (:)");
                                    put(
                                            "es",
                                            "Este usuario posee un nombre con caracteres inválidos (:)");
                                    put(
                                            "en-US",
                                            "This user has a name with invalid characters (:)");
                                }
                            });
                }
            };
}

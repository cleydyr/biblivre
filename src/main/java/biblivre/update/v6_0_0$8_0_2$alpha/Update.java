package biblivre.update.v6_0_0$8_0_2$alpha;

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

    @Override
    public void doUpdate(Connection connection) throws UpdateException {
        addTranslations();
    }

    private void addTranslations() {
        for (Map.Entry<String, Map<String, String>> entry : TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> translation : entry.getValue().entrySet()) {
                translationBO.addSingleTranslation(
                        translation.getKey(), entry.getKey(), translation.getValue());
            }
        }
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    private static final Map<String, Map<String, String>> TRANSLATIONS =
            Map.of(
                    "administration.reports.field.inactive_users",
                    Map.of("pt-BR", "Inativos", "es", "Inactivos", "en-US", "Inactive users"),
                    "administration.reports.field.no_users_for_criteria",
                    Map.of(
                            "pt-BR",
                            "Não há usuários com esse critério",
                            "es",
                            "No hay usuarios con este criterio",
                            "en-US",
                            "There are no users matching this criterion"));
}

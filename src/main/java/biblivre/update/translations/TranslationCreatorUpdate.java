package biblivre.update.translations;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class TranslationCreatorUpdate implements UpdateService {

    @Override
    public void doUpdate(Connection connection) {
        addTranslations(connection);
    }

    private void addTranslations(Connection connection) throws UpdateException {
        for (TranslationModel entry : getAdditionalTranslations()) {
            translationBO.addSingleTranslation(entry.language(), entry.key(), entry.text());
        }
    }

    public abstract Collection<TranslationModel> getAdditionalTranslations();

    private TranslationBO translationBO;

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }
}

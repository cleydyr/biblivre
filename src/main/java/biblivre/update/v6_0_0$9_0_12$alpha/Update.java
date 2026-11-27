package biblivre.update.v6_0_0$9_0_12$alpha;

import biblivre.update.translations.TranslationCreatorUpdate;
import biblivre.update.translations.TranslationModel;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Component;

/** Message shown when creating a backup fails. */
@Component
public class Update extends TranslationCreatorUpdate {

    static final String KEY = "administration.maintenance.backup.error.couldnt_create_backup";

    @Override
    public Collection<TranslationModel> getAdditionalTranslations() {
        return ADDITIONAL_TRANSLATIONS;
    }

    static final Collection<TranslationModel> ADDITIONAL_TRANSLATIONS =
            List.of(
                    new TranslationModel(KEY, "pt-BR", "Não foi possível criar o backup"),
                    new TranslationModel(KEY, "en-US", "The backup could not be created"),
                    new TranslationModel(KEY, "es", "No fue posible crear el backup"));
}

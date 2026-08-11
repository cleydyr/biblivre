package biblivre.update.v6_0_0$9_0_3$alpha;

import biblivre.update.translations.TranslationCreatorUpdate;
import biblivre.update.translations.TranslationModel;
import java.util.Collection;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class Update extends TranslationCreatorUpdate {

    @Override
    public Collection<TranslationModel> getAdditionalTranslations() {
        return ADDITIONAL_TRANSLATIONS;
    }

    private static final Collection<TranslationModel> ADDITIONAL_TRANSLATIONS =
            Set.of(
                    new TranslationModel("circulation.user_field.fines", "en-US", "Fines"),
                    new TranslationModel("circulation.user_field.fines", "es", "Multas"),
                    new TranslationModel("circulation.user_field.fines", "pt-BR", "Multas"));
}

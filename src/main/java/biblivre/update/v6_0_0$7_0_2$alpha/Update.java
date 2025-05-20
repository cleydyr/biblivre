package biblivre.update.v6_0_0$7_0_2$alpha;

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
                    new TranslationModel("search.holding.id", "en-US", "Holding ID"),
                    new TranslationModel("search.holding.id", "es", "N&ordm; de ejemplar"),
                    new TranslationModel("search.holding.id", "pt-BR", "N&ordm; do exemplar"));
}

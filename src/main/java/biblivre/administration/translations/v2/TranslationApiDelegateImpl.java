package biblivre.administration.translations.v2;

import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsMap;
import biblivre.generated.api.TranslationsApiDelegate;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TranslationApiDelegateImpl implements TranslationsApiDelegate {
    @Autowired private TranslationBO translationBO;

    @Override
    public ResponseEntity<Map<String, String>> getTranslations(String language) {
        TranslationsMap translations = translationBO.get(language);

        return ResponseEntity.ok(translations.getAllValues());
    }
}

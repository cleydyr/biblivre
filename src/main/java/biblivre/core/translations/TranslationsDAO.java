package biblivre.core.translations;

import java.util.List;
import java.util.Map;

public interface TranslationsDAO {

    List<TranslationDTO> list();

    List<TranslationDTO> list(String language);

    boolean save(Map<String, Map<String, String>> translations, int loggedUser);

    boolean save(
            Map<String, Map<String, String>> translations,
            Map<String, Map<String, String>> removeTranslations,
            int loggedUser);
}

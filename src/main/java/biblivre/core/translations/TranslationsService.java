package biblivre.core.translations;

import java.util.HashMap;
import java.util.List;

public interface TranslationsService {

	List<TranslationDTO> list();

	List<TranslationDTO> list(String language);

	boolean save(HashMap<String, HashMap<String, String>> translations, int loggedUser);

	boolean save(HashMap<String, HashMap<String, String>> translations,
			HashMap<String, HashMap<String, String>> removeTranslations, int loggedUser);

}
/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core.translations;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.StaticBO;
import biblivre.core.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Languages extends StaticBO {
    private static Logger logger = LoggerFactory.getLogger(Languages.class);

    private static Map<String, Set<LanguageDTO>> languages;

    private Languages() {}

    static {
        Languages.resetAll();
    }

    public static void resetAll() {
        Languages.languages = new HashMap<>();
    }

    public static void reset() {
        String schema = SchemaThreadLocal.get();

        if (schema.equals(Constants.GLOBAL_SCHEMA)) {
            Languages.resetAll();
        } else {
            Languages.languages.remove(schema);
        }
    }

    public static Set<LanguageDTO> getLanguages() {
        String schema = SchemaThreadLocal.get();

        Set<LanguageDTO> set = Languages.languages.get(schema);

        if (set == null) {
            set = Languages.loadLanguages(schema);
        }

        return set;
    }

    public static boolean isLoaded(String language) {
        if (StringUtils.isBlank(language)) {
            return false;
        }

        Set<LanguageDTO> languages = Languages.getLanguages();

        if (languages == null) {
            return false;
        }

        for (LanguageDTO dto : languages) {
            if (language.equals(dto.getLanguage())) {
                return true;
            }
        }

        return false;
    }

    public static boolean isNotLoaded(String language) {
        return !Languages.isLoaded(language);
    }

    public static String getDefaultLanguage() {
        Set<LanguageDTO> languages = Languages.getLanguages();

        for (LanguageDTO dto : languages) {
            return dto.getLanguage();
        }

        return "";
    }

    public static LanguageDTO getLanguage(String language) {
        Set<LanguageDTO> languages = Languages.getLanguages();

        for (LanguageDTO dto : languages) {
            if (language.equals(dto.getLanguage())) {
                return dto;
            }
        }

        return null;
    }

    private static synchronized Set<LanguageDTO> loadLanguages(String schema) {
        Set<LanguageDTO> set = Languages.languages.get(schema);

        // Checking again for thread safety.
        if (set != null) {
            return set;
        }

        Languages.logger.debug("Loading languages from " + schema);
        LanguagesDAO dao = LanguagesDAOImpl.getInstance();

        set = dao.list();
        Languages.languages.put(schema, set);

        return set;
    }
}

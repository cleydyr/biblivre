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
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class TranslationBO {
    private static final Logger logger = LoggerFactory.getLogger(TranslationBO.class);

    private static final Set<String> availableJavascriptLocales = Set.of("es-US", "es", "pt-BR");

    private TranslationsDAO translationsDAO;

    public TranslationsMap get(String language) {
        String schema = SchemaThreadLocal.get();

        return loadLanguage(schema, language);
    }

    public boolean save(
            String language,
            Map<String, String> translation,
            Map<String, String> removeTranslation,
            int loggedUser) {
        Map<String, Map<String, String>> translations = new HashMap<>();
        translations.put(language, translation);

        Map<String, Map<String, String>> removeTranslations = null;

        if (removeTranslation != null) {
            removeTranslations = new HashMap<>();
            removeTranslations.put(language, removeTranslation);
        }

        return save(translations, removeTranslations, loggedUser);
    }

    public boolean save(
            Map<String, Map<String, String>> translations,
            Map<String, Map<String, String>> removeTranslations,
            int loggedUser) {
        TranslationsDAOImpl.getInstance().save(translations, removeTranslations, loggedUser);

        return true;
    }

    public boolean addSingleTranslation(String language, String key, String text) {
        return SchemaThreadLocal.withGlobalSchema(
                () -> addSingleTranslation(language, key, text, Constants.ADMIN_LOGGED_USER_ID));
    }

    public boolean addSingleTranslation(String language, String key, String text, int loggedUser) {
        Map<String, String> translation = new HashMap<>();
        translation.put(key, text);

        Map<String, Map<String, String>> translations = new HashMap<>();
        translations.put(language, translation);

        return TranslationsDAOImpl.getInstance().save(translations, loggedUser);
    }

    public Locale toLocale(String locale) {
        if (locale == null) {
            return null;
        }

        locale = locale.replaceAll("-", "_");

        try {
            return LocaleUtils.toLocale(locale);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isJavaScriptLocaleUnavailable(String language) {
        if (language == null) {
            return true;
        }

        if (TranslationBO.availableJavascriptLocales.contains(language)) {
            return false;
        }

        return !TranslationBO.availableJavascriptLocales.contains(language.split("-")[0]);
    }

    public boolean isJavaLocaleAvailable(String language) {
        Locale locale = toLocale(language);

        if (locale == null) {
            return false;
        }

        if (LocaleUtils.isAvailableLocale(locale)) {
            return true;
        }

        locale = toLocale(language.split("-")[0]);

        return LocaleUtils.isAvailableLocale(locale);
    }

    public DiskFile createDumpFile(String language, ITemplateEngine templateEngine) {
        Map<String, TranslationDTO> translations = get(language).getAll();

        List<String> list = new ArrayList<>(translations.keySet());

        list.sort(new NamespaceComparator());

        try {
            File file = File.createTempFile("biblivre_translations_" + language + "_", ".txt");

            FileWriter out = new FileWriter(file);

            Map<String, Object> root = new HashMap<>();

            root.put("translations", translations);

            Context context = new Context(toLocale(language), root);

            templateEngine.process("translations_dump", context, out);

            out.flush();

            out.close();

            return new DiskFile(file, "x-download");
        } catch (Exception e) {
            TranslationBO.logger.error(e.getMessage(), e);
        }
        return null;
    }

    private TranslationsMap loadLanguage(String schema, String language) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading language " + schema + "." + language);
        }

        final TranslationsMap globalTranslationsMap =
                Constants.GLOBAL_SCHEMA.equals(schema)
                        ? null
                        : loadLanguage(Constants.GLOBAL_SCHEMA, language);

        return SchemaThreadLocal.withSchema(
                schema,
                () -> translationsDAO.getTranslationsMap(schema, language, globalTranslationsMap));
    }

    @Autowired
    public void setTranslationsDAO(TranslationsDAO translationsDAO) {
        this.translationsDAO = translationsDAO;
    }
}

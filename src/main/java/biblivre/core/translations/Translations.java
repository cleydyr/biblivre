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

import biblivre.core.FreemarkerTemplateHelper;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.StaticBO;
import biblivre.core.file.DiskFile;
import biblivre.core.utils.Constants;
import freemarker.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Translations extends StaticBO {
    private static Logger logger = LoggerFactory.getLogger(Translations.class);

    private static Map<Pair<String, String>, TranslationsMap> translations;

    private static Set<String> availableJavascriptLocales = Set.of("es-US", "es", "pt-BR");

    private Translations() {}

    static {
        Translations.reset();
    }

    public static void reset() {
        Translations.translations = new ConcurrentHashMap<>();
    }

    public static void reset(String language) {
        String schema = SchemaThreadLocal.get();

        Pair<String, String> pair = Pair.of(schema, language);

        Translations.translations.remove(pair);
    }

    public static TranslationsMap get(String language) {
        String schema = SchemaThreadLocal.get();

        Pair<String, String> pair = Pair.of(schema, language);

        return Translations.translations.computeIfAbsent(
                pair, __ -> loadLanguage(schema, language));
    }

    public static boolean save(
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

        return Translations.save(translations, removeTranslations, loggedUser);
    }

    public static boolean save(
            Map<String, Map<String, String>> translations,
            Map<String, Map<String, String>> removeTranslations,
            int loggedUser) {
        TranslationsDAOImpl.getInstance().save(translations, removeTranslations, loggedUser);

        for (String language : translations.keySet()) {
            Translations.reset(language);
        }

        Languages.reset();

        return true;
    }

    public static boolean addSingleTranslation(String language, String key, String text) {
        return SchemaThreadLocal.withSchema(
                Constants.GLOBAL_SCHEMA,
                () -> {
                    return addSingleTranslation(
                            language, key, text, Constants.ADMIN_LOGGED_USER_ID);
                });
    }

    public static boolean addSingleTranslation(
            String language, String key, String text, int loggedUser) {
        Map<String, String> translation = new HashMap<>();
        translation.put(key, text);

        Map<String, Map<String, String>> translations = new HashMap<>();
        translations.put(language, translation);

        boolean success = TranslationsDAOImpl.getInstance().save(translations, loggedUser);

        Translations.reset(language);

        return success;
    }

    public static Locale toLocale(String locale) {
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

    public static boolean isJavaScriptLocaleAvailable(String language) {
        if (language == null) {
            return false;
        }

        if (Translations.availableJavascriptLocales.contains(language)) {
            return true;
        }

        return Translations.availableJavascriptLocales.contains(language.split("-")[0]);
    }

    public static boolean isJavaLocaleAvailable(String language) {
        Locale locale = Translations.toLocale(language);

        if (locale == null) {
            return false;
        }

        if (LocaleUtils.isAvailableLocale(locale)) {
            return true;
        }

        locale = Translations.toLocale(language.split("-")[0]);

        return LocaleUtils.isAvailableLocale(locale);
    }

    public static DiskFile createDumpFile(String language) {
        Translations.reset(language);

        Map<String, TranslationDTO> translations = Translations.get(language).getAll();
        List<String> list = new ArrayList<>(translations.keySet());

        Collections.sort(list, new NamespaceComparator());

        try {
            File file = File.createTempFile("biblivre_translations_" + language + "_", ".txt");
            FileWriter out = new FileWriter(file);
            Template template =
                    FreemarkerTemplateHelper.freemarkerConfiguration.getTemplate(
                            "translations_dump.ftl");
            Map<String, Object> root = new HashMap<>();

            root.put("translations", translations);

            template.process(root, out);

            out.flush();

            out.close();

            return new DiskFile(file, "x-download");
        } catch (Exception e) {
            e.printStackTrace();
            Translations.logger.error(e.getMessage(), e);
        }
        return null;
    }

    private static TranslationsMap loadLanguage(String schema, String language) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading language " + schema + "." + language);
        }

        return SchemaThreadLocal.withSchema(
                schema,
                () -> {
                    if (StringUtils.isBlank(language)) {
                        return new TranslationsMap(schema, language, 1);
                    }

                    TranslationsDAOImpl dao = TranslationsDAOImpl.getInstance();

                    Collection<TranslationDTO> list = dao.list(language);

                    TranslationsMap translationsMap =
                            new TranslationsMap(schema, language, list.size());

                    for (TranslationDTO dto : list) {
                        translationsMap.put(dto.getKey(), dto);
                    }

                    return translationsMap;
                });
    }
}

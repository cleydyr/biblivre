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
package biblivre.administration.translations;

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.LanguageDTO;
import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.ITemplateEngine;

@Component("biblivre.administration.translations.Handler")
public class Handler extends AbstractHandler {
    private ITemplateEngine templateEngine;
    private LanguageBO languageBO;
    private TranslationBO translationBO;

    public void dump(ExtendedRequest request, ExtendedResponse response) {

        String language = request.getString("language");

        if (StringUtils.isBlank(language)) {
            this.setMessage(
                    ActionResult.WARNING, "administration.translations.error.invalid_language");
            return;
        }

        String dumpId = UUID.randomUUID().toString();

        request.setScopedSessionAttribute(dumpId, language);

        try {
            put("uuid", dumpId);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

    public void downloadDump(ExtendedRequest request, ExtendedResponse response) {

        String dumpId = request.getString("id");
        String language = (String) request.getScopedSessionAttribute(dumpId);

        if (StringUtils.isBlank(language)) {
            this.setMessage(
                    ActionResult.WARNING, "administration.translations.error.invalid_language");
            return;
        }

        final DiskFile exportFile = translationBO.createDumpFile(language, templateEngine);

        exportFile.setName("biblivre_translations_" + System.currentTimeMillis() + "_" + ".txt");

        this.setFile(exportFile);

        this.setCallback(exportFile::delete);
    }

    public void load(ExtendedRequest request, ExtendedResponse response) {
        int loggedUser = request.getLoggedUserId();
        boolean loadUserCreated = StringUtils.isNotBlank(request.getString("user_created"));
        Map<String, String> addTranslation = new HashMap<>();
        Map<String, String> removeTranslation = new HashMap<>();
        char type = '\0';
        String key = null;
        String value = null;

        try (Scanner sc =
                new Scanner(
                        request.getFile("file").getInputStream(),
                        Constants.DEFAULT_CHARSET.name())) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.length() == 0) {
                    continue;
                }

                line = line.replace("\\n", "\n");

                int eq = line.indexOf("=");
                char start = line.charAt(0);

                if (start == '#') {
                    continue;
                }

                if ((eq != -1) && (start == '*' || start == '+' || start == '-')) {
                    if (key != null) {
                        switch (type) {
                            case '*':
                                addTranslation.put(key, value);
                                break;
                            case '+':
                                if (loadUserCreated) {
                                    addTranslation.put(key, value);
                                }
                                break;
                            case '-':
                                removeTranslation.put(key, value);
                                break;
                        }
                    }
                    type = start;
                    key = line.substring(1, eq).trim();
                    value = line.substring(eq + 1).trim();
                } else if (StringUtils.isNotBlank(line)) {
                    value += "\n" + line;
                }
            }

            if (key != null) {
                switch (type) {
                    case '*':
                        addTranslation.put(key, value);
                        break;
                    case '+':
                        if (loadUserCreated) {
                            addTranslation.put(key, value);
                        }
                        break;
                    case '-':
                        removeTranslation.put(key, value);
                        break;
                }
            }

            sc.close();
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "administration.translations.error.load");
            return;
        }

        String language = addTranslation.get("language_code");

        if (StringUtils.isBlank(language)) {
            this.setMessage(
                    ActionResult.WARNING,
                    "administration.translations.error.no_language_code_specified");
            return;
        }

        if (!translationBO.isJavaScriptLocaleAvailable(language)) {
            this.setMessage(
                    ActionResult.WARNING,
                    "administration.translations.error.javascript_locale_not_available");
            return;
        }

        if (!translationBO.isJavaLocaleAvailable(language)) {
            this.setMessage(
                    ActionResult.WARNING,
                    "administration.translations.error.java_locale_not_available");
            return;
        }

        try {
            boolean success =
                    translationBO.save(language, addTranslation, removeTranslation, loggedUser);
            if (success) {
                this.setMessage(ActionResult.SUCCESS, "administration.translations.success.save");
            } else {
                this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
            }
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
            return;
        }
    }

    public void list(ExtendedRequest request, ExtendedResponse response) {
        Set<LanguageDTO> languages = languageBO.getLanguages();

        Map<String, Map<String, String>> translations = new HashMap<>();

        languages.forEach(
                languageDTO -> {
                    String language = languageDTO.getLanguage();

                    TranslationsMap translationsMap = translationBO.get(language);
                    Map<String, String> translation = translationsMap.getAllValues();

                    translations.put(language, translation);
                });

        try {
            put("translations", new JSONObject(translations));
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
            return;
        }
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {
        int loggedUser = request.getLoggedUserId();
        String strJson = request.getString("json");

        try {
            JSONObject json = new JSONObject(strJson);
            JSONObject jsonTranslations = json.optJSONObject("translations");

            if (jsonTranslations == null) {
                this.setMessage(ActionResult.WARNING, "error.invalid_json");
                return;
            }

            Map<String, Map<String, String>> newTranslations = new HashMap<>();

            jsonTranslations
                    .keys()
                    .forEachRemaining(
                            language -> {
                                JSONObject jsonTranslation =
                                        jsonTranslations.optJSONObject(language);

                                if (jsonTranslation != null) {
                                    Map<String, String> newTranslation = new HashMap<>();

                                    jsonTranslation
                                            .keys()
                                            .forEachRemaining(
                                                    key ->
                                                            newTranslation.put(
                                                                    key,
                                                                    jsonTranslation.optString(
                                                                            key)));

                                    newTranslations.put(language, newTranslation);
                                }
                            });

            translationBO.save(newTranslations, null, loggedUser);

            this.setMessage(ActionResult.SUCCESS, "administration.translations.success.save");
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
            return;
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
            return;
        }
    }

    public void saveLanguageTranslations(ExtendedRequest request, ExtendedResponse response) {
        int loggedUser = request.getLoggedUserId();
        String strJson = request.getString("translations");

        try {
            JSONObject json = new JSONObject(strJson);
            String language = json.getString("language_code");

            if (!translationBO.isJavaScriptLocaleAvailable(language)) {
                this.setMessage(
                        ActionResult.WARNING, "administration.translations.error.invalid_language");
                return;
            }

            Map<String, String> newTranslation = new HashMap<>();

            json.keys().forEachRemaining(key -> newTranslation.put(key, json.optString(key)));

            translationBO.save(language, newTranslation, new HashMap<>(), loggedUser);

            this.setMessage(ActionResult.SUCCESS, "administration.translations.success.save");

        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
            return;
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "administration.translations.error.save");
            return;
        }
    }

    @Autowired
    public void setTemplateEngine(ITemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Autowired
    public void setLanguageBO(LanguageBO languageBO) {
        this.languageBO = languageBO;
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }
}

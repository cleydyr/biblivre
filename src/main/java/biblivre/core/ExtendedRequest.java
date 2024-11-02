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
package biblivre.core;

import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.file.MemoryFile;
import biblivre.core.translations.LanguageBO;
import biblivre.core.translations.TranslationBO;
import biblivre.core.translations.TranslationsMap;
import biblivre.login.LoginDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

public class ExtendedRequest extends HttpServletRequestWrapper {
    private static final Logger logger = LoggerFactory.getLogger(ExtendedRequest.class);

    @Getter private TranslationsMap translationsMap;

    @Setter private String controller;
    private String language;
    private boolean mustRedirectToSchema;
    @Getter private boolean multiPart;
    private Map<String, String> multiPartParameters;
    private Map<String, MemoryFile> multiPartFiles;

    private final TranslationBO translationBO;

    public ExtendedRequest(
            HttpServletRequest request,
            RequestParserHelper requestParserHelper,
            LanguageBO languageBO,
            TranslationBO translationBO)
            throws IOException, ServletException {
        super(request);

        this.translationBO = translationBO;

        String servletPath = request.getServletPath();

        if (servletPath.contains("static/")) {
            return;
        }

        this.loadMultiPart(request);

        String requestPath = this.getRequestURI().substring(this.getContextPath().length() + 1);

        this.setController(
                requestParserHelper.parseController(requestPath, getString("controller")));
        this.setMustRedirectToSchema(requestParserHelper.isMustRedirectToSchema(requestPath));
        this.loadLanguage(languageBO::isNotLoaded, languageBO.getDefaultLanguage());
        this.loadTranslationsMap();
        this.setAttribute("languageBO", languageBO);
    }

    public String getLocalizedText(String key) {
        String[] params = key.split(":::");

        if (params.length == 1) {
            return this.translationsMap.getText(key);
        }

        String text = this.translationsMap.getText(params[0]);
        for (int i = 1; i < params.length; i++) {
            String replacement = params[i];
            text = text.replace("{" + (i - 1) + "}", replacement);
        }
        return text;
    }

    public void dispatch(String url, ExtendedResponse response)
            throws ServletException, IOException {
        this.getRequestDispatcher(url).forward(this, response);
    }

    public void setScopedSessionAttribute(String key, Object obj) {
        String schema = SchemaThreadLocal.get();

        setSessionAttribute(schema + "." + key, obj);
    }

    public void setSessionAttribute(String key, Object obj) {
        this.getSession().setAttribute(key, obj);
    }

    public Object getSessionAttribute(String key) {
        return this.getSession().getAttribute(key);
    }

    public Object getScopedSessionAttribute(String key) {
        String schema = SchemaThreadLocal.get();

        return this.getSessionAttribute(schema + "." + key);
    }

    public boolean hasParameter(String key) {
        return this.getParameterMap().containsKey(key);
    }

    public final String getString(String key) {
        return this.getString(key, "");
    }

    public final String getString(String key, String defaultValue) {
        String value = this.getRequestParameter(key);

        return Objects.toString(value, defaultValue);
    }

    public Integer getInteger(String key) {
        return this.getInteger(key, 0);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Integer retValue = defaultValue;

        String parameter = this.getRequestParameter(key);

        if (parameter == null || parameter.isBlank()) {
            return defaultValue;
        }

        try {
            retValue = Integer.valueOf(parameter);
        } catch (NumberFormatException numberFormatException) {
            logger.warn("parameter " + key + " is not a valid number");
        }

        return retValue;
    }

    public Float getFloat(String key) {
        return this.getFloat(key, 0.0f);
    }

    public Float getFloat(String key, Float defaultValue) {
        try {
            return Float.parseFloat(this.getRequestParameter(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBoolean(String key) {
        return this.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(this.getRequestParameter(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public <T extends Enum<T>> T getEnum(Class<T> type, String key) {
        return this.getEnum(type, key, null);
    }

    public <T extends Enum<T>> T getEnum(Class<T> type, String key, T defaultValue) {
        String value = this.getString(key);

        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public MemoryFile getFile(String key) {
        return this.multiPart ? this.multiPartFiles.get(key) : null;
    }

    public String getRequestParameter(String key) {
        return this.multiPart ? this.multiPartParameters.get(key) : super.getParameter(key);
    }

    public String getController() {
        return StringUtils.defaultString(this.controller);
    }

    public void setLanguage(String language) {
        this.language = language;
        this.setAttribute("language", language);
    }

    public String getLanguage() {
        return this.language;
    }

    public Locale getSelectedLocale() {
        Locale locale = translationBO.toLocale(this.getLanguage());

        return (locale == null) ? this.getLocale() : locale;
    }

    public void setMustRedirectToSchema(boolean mustRedirectToSchema) {
        this.mustRedirectToSchema = mustRedirectToSchema;
    }

    public boolean mustRedirectToSchema() {
        return this.mustRedirectToSchema;
    }

    public void setTranslationsMap(TranslationsMap translationsMap) {
        this.translationsMap = translationsMap;
        this.setAttribute("translationsMap", translationsMap);
    }

    public int getLoggedUserId() {
        Object user = this.getScopedSessionAttribute("logged_user");

        if (user instanceof LoginDTO login) {
            return login.getId();
        }

        return 0;
    }

    private final void loadMultiPart(HttpServletRequest httpServletRequest)
            throws IOException, ServletException {
        String contentType = httpServletRequest.getContentType();

        this.multiPart =
                contentType != null && contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE);

        if (this.multiPart) {
            this.multiPartParameters = new HashMap<>();
            this.multiPartFiles = new HashMap<>();

            for (Part part : httpServletRequest.getParts()) {
                String filename = part.getSubmittedFileName();

                if (filename != null) {
                    this.multiPartFiles.put(
                            part.getName(),
                            new MemoryFile(
                                    part.getSubmittedFileName(),
                                    part.getContentType(),
                                    part.getSize(),
                                    part.getInputStream()));
                } else {
                    this.multiPartParameters.put(
                            part.getName(), new String(part.getInputStream().readAllBytes()));
                }
            }
        }
    }

    public void clearSessionAttributes(String schema) {
        Enumeration<String> atributes = this.getSession().getAttributeNames();
        while (atributes.hasMoreElements()) {
            String attribute = atributes.nextElement();
            if (StringUtils.defaultString(attribute).startsWith("schema.")) {
                this.getSession().removeAttribute(attribute);
            }
        }
    }

    private void loadLanguage(Predicate<String> isNotLoaded, String defaultLanguage) {
        HttpSession session = this.getSession();

        String language = this.getString("i18n");

        if (isNotLoaded.test(language)) {
            String schema = SchemaThreadLocal.get();

            language = (String) session.getAttribute(schema + ".language");
        }

        if (isNotLoaded.test(language)) {
            language = (String) session.getAttribute("global.language");
        }

        if (isNotLoaded.test(language)) {
            language = this.getLocale().toString().replaceAll("[_]", "-");
        }

        if (isNotLoaded.test(language)) {
            language = "pt-BR";
        }

        if (isNotLoaded.test(language)) {
            language = defaultLanguage;
        }

        this.setScopedSessionAttribute("language", language);

        this.setLanguage(language);
    }

    private void loadTranslationsMap() {
        this.setTranslationsMap(getTranslationMap());
    }

    private TranslationsMap getTranslationMap() {
        return translationBO.get(this.language);
    }

    @Deprecated
    @Override
    public String getParameter(String name) {
        return super.getParameter(name);
    }

    public AuthorizationPoints getAuthorizationPoints() {
        return (AuthorizationPoints) getScopedSessionAttribute("logged_user_atps");
    }
}

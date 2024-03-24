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

import biblivre.core.IFCacheableJavascript;
import biblivre.core.JavascriptCache;
import biblivre.core.utils.Constants;
import java.io.File;
import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TranslationsMap extends HashMap<String, TranslationDTO>
        implements IFCacheableJavascript {
    private static final Logger logger = LoggerFactory.getLogger(TranslationsMap.class);
    @Serial private static final long serialVersionUID = 1L;

    private String schema;
    private String language;
    private JavascriptCache cache;
    private final TranslationsMap globalTranslationsMap;

    public TranslationsMap(
            String schema,
            String language,
            TranslationBO translationBO,
            TranslationsMap globalTranslationsMap) {
        this(schema, language, 16, globalTranslationsMap);
    }

    public TranslationsMap(
            String schema,
            String language,
            int initialSize,
            TranslationsMap globalTranslationsMap) {
        super(initialSize);

        this.globalTranslationsMap = globalTranslationsMap;
        this.setSchema(schema);
        this.setLanguage(language);
    }

    public String getText(Object key) {
        if (StringUtils.isBlank(key.toString())) {
            return "";
        }

        TranslationDTO dto = this.get(key);
        String value = null;

        if (dto != null) {
            value = dto.getText();
        }

        if (StringUtils.isEmpty(value)) {
            if (isNotGlobalSchema()) {
                value = globalTranslationsMap.getText(key);
            } else {
                logger.warn(
                        "Translation key not found: "
                                + this.schema
                                + "."
                                + this.language
                                + "."
                                + key);
                value = "__" + key + "__";
            }
        }

        return value;
    }

    private boolean isNotGlobalSchema() {
        return !this.getSchema().equals(Constants.GLOBAL_SCHEMA);
    }

    public String getHtml(Object key) {
        String value = this.getText(key);

        return StringEscapeUtils.escapeHtml4(value);
    }

    public String getSchema() {
        return Objects.toString(this.schema, Constants.GLOBAL_SCHEMA);
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getLanguage() {
        return StringUtils.defaultString(this.language);
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, TranslationDTO> getAll() {
        Map<String, TranslationDTO> translations = new HashMap<>();

        if (isNotGlobalSchema()) {
            translations.putAll(globalTranslationsMap);
        }

        for (Map.Entry<String, TranslationDTO> e : this.entrySet()) {
            String key = e.getKey();
            TranslationDTO dto = e.getValue();

            if (!StringUtils.isEmpty(dto.getText()) || !translations.containsKey(key)) {
                translations.put(key, dto);
            }
        }

        return translations;
    }

    public Map<String, String> getAllValues() {
        Map<String, TranslationDTO> all = this.getAll();
        Map<String, String> translation = new HashMap<>();

        all.forEach((key, translationDTO) -> translation.put(key, translationDTO.getText()));

        return translation;
    }

    @Override
    public String toJavascriptString() {
        return "Translations.translations = " + this + ";";
    }

    @Override
    public String getCacheFileNamePrefix() {
        return this.getSchema() + "." + this.getLanguage();
    }

    @Override
    public String getCacheFileNameSuffix() {
        return ".i18n.js";
    }

    @Override
    public File getCacheFile() {
        if (this.cache == null) {
            this.cache = new JavascriptCache(this);
        }

        return this.cache.getCacheFile();
    }

    @Override
    public String getCacheFileName() {
        if (this.cache == null) {
            this.cache = new JavascriptCache(this);
        }

        return this.cache.getFileName();
    }

    @Override
    public void invalidateCache() {
        this.cache = null;
    }

    @Override
    public String toString() {
        return new JSONObject(this.getAllValues()).toString();
    }
}

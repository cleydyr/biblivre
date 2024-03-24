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
package biblivre.core.configurations;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.translations.TranslationBO;
import biblivre.core.utils.Constants;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationBO {
    private ConfigurationsDAO configurationsDAO;

    private static final Logger logger = LoggerFactory.getLogger(TranslationBO.class);

    public String getString(String key) {
        return getValue(key);
    }

    public String getHtml(String key, String schema) {
        String value = getValue(key, schema);

        return StringEscapeUtils.escapeHtml4(value);
    }

    private String getValue(String key, String schema) {
        ConfigurationsDTO config = get(key, schema);

        String value = "";

        if (config != null) {
            value = config.getValue();
        }

        return StringUtils.defaultString(value);
    }

    public String getHtml(String key) {
        String value = getValue(key);

        return StringEscapeUtils.escapeHtml4(value);
    }

    public int getInt(String key) {
        return getInt(key, 0);
    }

    public int getPositiveInt(String key, int def) {
        int ret = getInt(key, def);
        return ret > 0 ? ret : def;
    }

    public int getInt(String key, int def) {
        String value = getValue(key);

        if (StringUtils.isBlank(value)) {
            logger.debug("Configuration is empty: " + key);

            return def;
        }

        String schema = SchemaThreadLocal.get();

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.warn("Configuration is not an integer: " + schema + "." + key + " = " + value);
            return def;
        }
    }

    public float getFloat(String key) {
        String schema = SchemaThreadLocal.get();

        String value = getValue(key);

        try {
            return Float.parseFloat(value.replace(',', '.'));
        } catch (Exception e) {
            logger.warn("Configuration is not a float: " + schema + "." + key + " = " + value);
            return 0.0f;
        }
    }

    public boolean getBoolean(String key) {
        String value = getValue(key);

        return value.equals("true");
    }

    public List<Integer> getIntArray(String key, String def) {
        String value = getValue(key);

        if (StringUtils.isBlank(value)) {
            value = def;
        }

        try {
            return stringToIntArray(value);
        } catch (Exception e) {
            return stringToIntArray(def);
        }
    }

    private List<Integer> stringToIntArray(String string) {
        String[] array = string.split(",");

        List<Integer> list = new ArrayList<>(array.length);

        for (String val : array) {
            list.add(Integer.valueOf(val));
        }

        return list;
    }

    public boolean isMultipleSchemasEnabled() {
        return SchemaThreadLocal.withGlobalSchema(() -> getBoolean(Constants.CONFIG_MULTI_SCHEMA));
    }

    public List<ConfigurationsDTO> validate(
            List<ConfigurationsDTO> configs,
            boolean multipleSchemasEnabled,
            long countEnabledSchemas)
            throws ValidationException {
        List<ConfigurationsDTO> validConfigs = new ArrayList<>(configs.size());
        ValidationException e = new ValidationException("administration.error.invalid");
        boolean errors = false;

        String schema = SchemaThreadLocal.get();

        for (ConfigurationsDTO config : configs) {
            if (config.getKey().equals(Constants.CONFIG_MULTI_SCHEMA)
                    && config.getValue().equals("false")) {

                if (!schema.equals(Constants.GLOBAL_SCHEMA) && multipleSchemasEnabled) {
                    errors = true;
                    e.addError(
                            config.getKey(),
                            "multi_schema.error.disable_multi_schema_outside_global");
                    continue;
                }

                if (countEnabledSchemas > 1) {
                    errors = true;
                    e.addError(
                            config.getKey(),
                            "multi_schema.error.disable_multi_schema_schema_count");
                    continue;
                }

                validConfigs.add(config);
            }

            ConfigurationsDTO currentConfig = get(config.getKey());

            if (currentConfig == null) {
                config.setType("string");
                config.setRequired(false);
            } else if (currentConfig.getValue().equals(config.getValue())) {
                continue;
            } else {
                config.setType(currentConfig.getType());
                config.setRequired(currentConfig.isRequired());
            }

            String validationError = config.validate();
            if (validationError != null) {
                errors = true;
                e.addError(config.getKey(), validationError);
            } else {
                validConfigs.add(config);
            }
        }

        if (errors) {
            throw e;
        }

        return validConfigs;
    }

    public void save(List<ConfigurationsDTO> configs, int loggedUser) {
        ConfigurationsDTO multiSchemaConfig = null;

        for (Iterator<ConfigurationsDTO> it = configs.iterator(); it.hasNext(); ) {
            ConfigurationsDTO configDto = it.next();
            if (configDto.getKey().equals(Constants.CONFIG_MULTI_SCHEMA)) {
                multiSchemaConfig = configDto;
                it.remove();
                break;
            }
        }

        if (multiSchemaConfig != null) {
            final ConfigurationsDTO finalMultiSchemaConfig = multiSchemaConfig;

            SchemaThreadLocal.withGlobalSchema(
                    () -> {
                        List<ConfigurationsDTO> multiSchemaList = new ArrayList<>();

                        multiSchemaList.add(finalMultiSchemaConfig);

                        configurationsDAO.save(multiSchemaList, loggedUser);

                        Map<String, ConfigurationsDTO> map = getMap();

                        map.put(finalMultiSchemaConfig.getKey(), finalMultiSchemaConfig);

                        return null;
                    });
        }

        if (configurationsDAO.save(configs, loggedUser)) {
            Map<String, ConfigurationsDTO> map = getMap();

            for (ConfigurationsDTO config : configs) {
                map.put(config.getKey(), config);
            }
        }
    }

    public void save(ConfigurationsDTO config, int loggedUser) {
        save(List.of(config), loggedUser);
    }

    private String getValue(String key) {
        ConfigurationsDTO config = get(key);

        String value = "";

        if (config != null) {
            value = config.getValue();
        }

        return StringUtils.defaultString(value);
    }

    public void setMultipleSchemasEnabled(Integer loggedUser) {
        ConfigurationsDTO config =
                SchemaThreadLocal.withGlobalSchema(() -> get(Constants.CONFIG_MULTI_SCHEMA));

        config.setValue("true");

        save(config, loggedUser);
    }

    private ConfigurationsDTO get(String key) {
        Map<String, ConfigurationsDTO> map = getMap();

        ConfigurationsDTO config = map.get(key);

        if (config == null) {
            String schema = SchemaThreadLocal.get();

            if (schema.equals(Constants.GLOBAL_SCHEMA)) {
                return null;
            }

            return SchemaThreadLocal.withGlobalSchema(() -> get(key));
        }

        return config;
    }

    private ConfigurationsDTO get(String key, String schema) {
        Map<String, ConfigurationsDTO> map = getMap();

        ConfigurationsDTO config = map.get(key);

        if (config == null) {
            if (schema.equals(Constants.GLOBAL_SCHEMA)) {
                return null;
            }

            return SchemaThreadLocal.withGlobalSchema(() -> get(key));
        }

        return config;
    }

    private Map<String, ConfigurationsDTO> getMap() {
        List<ConfigurationsDTO> configs = configurationsDAO.list();

        Map<String, ConfigurationsDTO> map = new HashMap<>(configs.size());

        for (ConfigurationsDTO config : configs) {
            map.put(config.getKey(), config);
        }

        return map;
    }

    @Autowired
    public void setConfigurationsDAO(ConfigurationsDAO configurationsDAO) {
        this.configurationsDAO = configurationsDAO;
    }
}

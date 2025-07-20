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
package biblivre.circulation.user;

import biblivre.core.translations.TranslationBO;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserFieldService {

    private static final Logger logger = LoggerFactory.getLogger(UserFieldService.class);

    private final UserFieldsDAO userFieldsDAO;
    private final TranslationBO translationBO;

    @Autowired
    public UserFieldService(UserFieldsDAO userFieldsDAO, TranslationBO translationBO) {
        this.userFieldsDAO = userFieldsDAO;
        this.translationBO = translationBO;
    }

    /**
     * Creates a new user field with translations.
     *
     * @param userField The user field to persist
     * @param fieldNameTranslations Map of language codes to translated field names
     * @return The persisted UserFieldDTO
     */
    @Transactional
    public UserFieldDTO createUserFieldWithTranslations(
            UserFieldDTO userField, Map<String, String> fieldNameTranslations) {

        logger.info(
                "Creating user field '{}' with translations for {} languages",
                userField.getKey(),
                fieldNameTranslations.size());

        // 1. Persist the field in the users_fields table
        UserFieldDTO persistedField = userFieldsDAO.persistField(userField);

        // 2. Persist the field name translations in the global schema
        persistFieldNameTranslations(userField.getKey(), fieldNameTranslations);

        logger.info("Successfully created user field '{}' with translations", userField.getKey());

        return persistedField;
    }

    /**
     * Persists field name translations in the global schema.
     *
     * @param fieldKey The field key to create translation keys for
     * @param translations Map of language codes to translated field names
     */
    private void persistFieldNameTranslations(String fieldKey, Map<String, String> translations) {
        // Generate the translation key for this field
        String translationKey = "circulation.custom.user_field." + fieldKey;

        // Save translations in the global schema
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            String language = entry.getKey();
            String translatedName = entry.getValue();

            logger.debug(
                    "Adding translation for field '{}' in language '{}': '{}'",
                    fieldKey,
                    language,
                    translatedName);

            translationBO.addSingleTranslation(language, translationKey, translatedName);
        }
        return null;
    }

    /**
     * Updates translations for an existing user field.
     *
     * @param fieldKey The field key
     * @param fieldNameTranslations Map of language codes to translated field names
     */
    @Transactional
    public void updateFieldTranslations(
            String fieldKey, Map<String, String> fieldNameTranslations) {
        logger.info(
                "Updating translations for user field '{}' with {} languages",
                fieldKey,
                fieldNameTranslations.size());

        persistFieldNameTranslations(fieldKey, fieldNameTranslations);

        logger.info("Successfully updated translations for user field '{}'", fieldKey);
    }
}

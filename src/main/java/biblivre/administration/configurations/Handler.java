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
package biblivre.administration.configurations;

import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.enums.ActionResult;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.translations.TranslationBO;
import biblivre.core.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {

    private TranslationBO translationBO;
    private SchemaBO schemaBO;

    public void save(ExtendedRequest request, ExtendedResponse response) {
        int loggedUser = request.getLoggedUserId();
        String language = request.getLanguage();

        String configurations =
                request.getString("configurations", Constants.JSON_EMPTY_OBJECT_STR);
        final List<ConfigurationsDTO> configs = new ArrayList<>();

        JSONObject json = new JSONObject(configurations);

        json.keys()
                .forEachRemaining(
                        key -> {
                            String value = json.get(key).toString();

                            if (key.equals("text.main.logged_in")
                                    || key.equals("text.main.logged_out")) {
                                translationBO.addSingleTranslation(
                                        language, key, value, loggedUser);
                            } else {
                                configs.add(new ConfigurationsDTO(key, value));
                            }
                        });

        if (configs.size() == 0) {
            return;
        }

        boolean multiSchemaBefore = configurationBO.isMultipleSchemasEnabled();

        configurationBO.save(
                configurationBO.validate(
                        configs,
                        configurationBO.isMultipleSchemasEnabled(),
                        schemaBO.countEnabledSchemas()),
                loggedUser);

        boolean multiSchemaAfter = configurationBO.isMultipleSchemasEnabled();

        this.setMessage(ActionResult.SUCCESS, "administration.configurations.save.success");

        put("reload", multiSchemaBefore != multiSchemaAfter);
    }

    public void ignoreUpdate(ExtendedRequest request, ExtendedResponse response) {
        request.getSession().removeAttribute(".system_warning_new_version");
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }
}

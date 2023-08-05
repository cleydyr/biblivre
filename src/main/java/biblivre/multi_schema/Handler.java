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
package biblivre.multi_schema;

import biblivre.administration.setup.State;
import biblivre.core.AbstractHandler;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.schemas.SchemaBO;
import biblivre.core.schemas.SchemaDTO;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private SchemaBO schemaBO;

    public void create(ExtendedRequest request, ExtendedResponse response) {

        String titleParam = request.getString("title");
        String subtitleParam = request.getString("subtitle");
        String schemaParam = request.getString("schema");
        int loggedUserId = request.getLoggedUserId();

        SchemaDTO dto = new SchemaDTO();
        dto.setName(titleParam);
        dto.setSchema(schemaParam);
        dto.setCreatedBy(loggedUserId);

        State.start();
        State.writeLog(request.getLocalizedText("multi_schema.manage.log_header"));

        boolean success = schemaBO.createSchema(dto, true, subtitleParam);

        if (success) {
            State.finish();

            this.setMessage(ActionResult.SUCCESS, "multi_schema.manage.success.create");
        } else {
            State.cancel();

            this.setMessage(ActionResult.WARNING, "multi_schema.manage.error.create");
        }

        try {
            put("success", success);

            if (success) {
                put("data", dto.toJSONObject());
                put("full_data", true);
            } else {
                put("log", true);
            }
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void toggle(ExtendedRequest request, ExtendedResponse response) {
        String schemaParam = request.getString("schema");
        boolean disable = request.getBoolean("disable", false);

        SchemaDTO dto = schemaBO.getSchema(schemaParam);

        if (dto == null) {
            this.setMessage(ActionResult.WARNING, "multi_schema.manage.error.toggle");
            return;
        }

        if (disable) {
            if (schemaBO.countEnabledSchemas() == 1) {
                this.setMessage(
                        ActionResult.WARNING,
                        "multi_schema.manage.error.cant_disable_last_library");
                return;
            }
        }

        boolean success = disable ? schemaBO.disable(dto) : schemaBO.enable(dto);

        try {
            put("success", success);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void deleteSchema(ExtendedRequest request, ExtendedResponse response) {

        String schemaParam = request.getString("schema");

        SchemaDTO dto = schemaBO.getSchema(schemaParam);
        boolean success = schemaBO.deleteSchema(dto);

        try {
            put("success", success);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    @Autowired
    public void setSchemaBO(SchemaBO schemaBO) {
        this.schemaBO = schemaBO;
    }
}

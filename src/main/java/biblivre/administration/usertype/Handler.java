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
package biblivre.administration.usertype;

import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.search.SearchException;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private UserTypeBO userTypeBO;

    public void search(ExtendedRequest request, ExtendedResponse response) {

        String searchParameters = request.getString("search_parameters");

        String query;

        try {
            JSONObject json = new JSONObject(searchParameters);
            query = json.optString("query");
        } catch (JSONException je) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        Integer limit =
                request.getInteger(
                        "limit", configurationBO.getInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE));
        int offset = (request.getInteger("page", 1) - 1) * limit;

        DTOCollection<UserTypeDTO> list = userTypeBO.search(query, limit, offset);

        if (list == null || list.isEmpty()) {
            this.setMessage(
                    ActionResult.WARNING, "administration.user_type.error.no_user_type_found");
            return;
        }

        try {
            put("search", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        this.search(request, response);
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {

        Integer id = request.getInteger("id", 0);

        UserTypeDTO dto;

        if (id != 0) {
            dto = userTypeBO.get(id);
            if (dto == null) {
                this.setMessage(
                        ActionResult.WARNING, "administration.user_type.error.no_user_type_found");
                return;
            }
            dto.setModifiedBy(request.getLoggedUserId());
        } else {
            dto = new UserTypeDTO();
            dto.setCreatedBy(request.getLoggedUserId());
        }

        dto.setName(request.getString("name"));
        dto.setDescription(request.getString("description", ""));
        dto.setLendingLimit(request.getInteger("lending_limit"));
        dto.setReservationLimit(request.getInteger("reservation_limit"));
        dto.setLendingTimeLimit(request.getInteger("lending_time_limit"));
        dto.setReservationTimeLimit(request.getInteger("reservation_time_limit"));
        dto.setFineValue(request.getFloat("fine_value"));

        if (userTypeBO.save(dto)) {
            if (id == 0) {
                this.setMessage(ActionResult.SUCCESS, "administration.user_type.success.save");
            } else {
                this.setMessage(ActionResult.SUCCESS, "administration.user_type.success.update");
            }
        } else {
            this.setMessage(ActionResult.WARNING, "administration.user_type.error.save");
        }

        try {
            put("data", dto.toJSONObject());
            put("full_data", true);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        try {
            if (userTypeBO.delete(id)) {
                this.setMessage(ActionResult.SUCCESS, "administration.user_type.success.delete");
            } else {
                this.setMessage(ActionResult.WARNING, "administration.user_type.error.delete");
            }
        } catch (SearchException e) {
            this.setMessage(ActionResult.ERROR, "error.internal_error");
        }
    }

    @Autowired
    public void setUserTypeBO(UserTypeBO userTypeBO) {
        this.userTypeBO = userTypeBO;
    }
}

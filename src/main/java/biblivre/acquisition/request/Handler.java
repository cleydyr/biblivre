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
package biblivre.acquisition.request;

import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private RequestBO requestBO;
    private ConfigurationBO configurationBO;

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

        DTOCollection<RequestDTO> list = requestBO.search(query, limit, offset);

        if (list.size() == 0) {
            this.setMessage(ActionResult.WARNING, "acquisition.request.error.no_request_found");
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

    public void open(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        RequestDTO dto = requestBO.get(id);

        try {
            put("request", dto.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");
        RequestDTO dto = this.populateDTO(request);

        boolean result;
        if (id == 0) {
            dto.setStatus(RequestStatus.PENDING);
            dto.setCreatedBy(request.getLoggedUserId());
            result = requestBO.save(dto);
        } else {
            dto.setId(id);
            dto.setModifiedBy(request.getLoggedUserId());
            result = requestBO.update(dto);
        }
        if (result) {
            if (id == 0) {
                this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.save");
            } else {
                this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.update");
            }
        } else {
            this.setMessage(ActionResult.WARNING, "acquisition.request.error.save");
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

        RequestDTO dto = new RequestDTO();
        dto.setId(id);

        if (requestBO.delete(dto)) {
            this.setMessage(ActionResult.SUCCESS, "acquisition.request.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "acquisition.request.error.delete");
        }
    }

    private RequestDTO populateDTO(ExtendedRequest request) {
        RequestDTO dto = new RequestDTO();
        dto.setId(request.getInteger("id"));
        dto.setRequester(request.getString("requester"));
        dto.setAuthor(request.getString("author"));
        dto.setTitle(request.getString("title"));
        dto.setSubtitle(request.getString("subtitle"));
        dto.setEditionNumber(request.getString("edition"));
        dto.setPublisher(request.getString("publisher"));
        dto.setInfo(request.getString("info"));
        dto.setStatus(RequestStatus.fromString(request.getString("status")));
        dto.setQuantity(request.getInteger("quantity"));
        return dto;
    }

    @Autowired
    public void setRequestBO(RequestBO requestBO) {
        this.requestBO = requestBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}

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
package biblivre.acquisition.quotation;

import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private QuotationBO quotationBO;
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

        DTOCollection<QuotationDTO> list = quotationBO.search(query, limit, offset);

        if (list.size() == 0) {
            this.setMessage(ActionResult.WARNING, "acquisition.quotation.error.no_quotation_found");
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

        QuotationDTO dto = quotationBO.get(id);

        try {
            put("quotation", dto.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void list(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("supplier_id");

        DTOCollection<QuotationDTO> list = quotationBO.list(id);

        try {
            put("list", list.toJSONObject());
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");
        QuotationDTO dto;

        try {
            dto = this.populateDTO(request);
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        Integer newId = 0;
        boolean result = false;
        if (id == 0) {
            dto.setCreatedBy(request.getLoggedUserId());
            newId = quotationBO.save(dto);
        } else {
            dto.setId(id);
            dto.setModifiedBy(request.getLoggedUserId());
            result = quotationBO.update(dto);
        }
        if (newId != 0 || result) {
            if (id == 0) {
                this.setMessage(ActionResult.SUCCESS, "acquisition.quotation.success.save");
            } else {
                this.setMessage(ActionResult.SUCCESS, "acquisition.quotation.success.update");
            }
        } else {
            this.setMessage(ActionResult.WARNING, "acquisition.quotation.error.save");
        }

        dto = quotationBO.get(id == 0 ? newId : id);

        try {
            put("data", dto.toJSONObject());
            put("full_data", true);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        QuotationDTO dto = new QuotationDTO();
        dto.setId(id);

        if (quotationBO.delete(dto)) {
            this.setMessage(ActionResult.SUCCESS, "acquisition.quotation.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "acquisition.quotation.error.delete");
        }
    }

    private QuotationDTO populateDTO(ExtendedRequest request) throws Exception {
        QuotationDTO dto = new QuotationDTO();
        dto.setId(request.getInteger("id"));
        dto.setSupplierId(request.getInteger("supplier"));
        dto.setCreated(TextUtils.parseDate(request.getString("quotation_date")));
        dto.setResponseDate(TextUtils.parseDate(request.getString("response_date")));
        dto.setExpirationDate(TextUtils.parseDate(request.getString("expiration_date")));
        dto.setDeliveryTime(request.getInteger("delivery_time"));
        dto.setInfo(request.getString("info"));

        String searchParameters = request.getString("quotation_list");

        JSONArray quotationList = new JSONArray(searchParameters);
        List<RequestQuotationDTO> quotations = new ArrayList<>();
        for (int i = 0, imax = quotationList.length(); i < imax; i++) {
            JSONObject searchTerm = quotationList.optJSONObject(i);

            if (searchTerm == null) {
                continue;
            }

            int requestId = searchTerm.optInt("id");
            int quantity = searchTerm.optInt("quantity");
            Float value = Float.valueOf(searchTerm.optString("value"));

            RequestQuotationDTO rqdto = new RequestQuotationDTO();
            rqdto.setRequestId(requestId);
            rqdto.setQuantity(quantity);
            rqdto.setUnitValue(value);
            quotations.add(rqdto);
        }
        dto.setQuotationsList(quotations);

        return dto;
    }

    @Autowired
    public void setQuotationBO(QuotationBO quotationBO) {
        this.quotationBO = quotationBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }
}

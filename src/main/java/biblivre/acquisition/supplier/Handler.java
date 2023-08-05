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
package biblivre.acquisition.supplier;

import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Handler extends AbstractHandler {
    private SupplierBO supplierBO;

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

        DTOCollection<SupplierDTO> list = supplierBO.search(query, limit, offset);

        if (list.size() == 0) {
            this.setMessage(ActionResult.WARNING, "acquisition.supplier.error.no_supplier_found");
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

        SupplierDTO dto = supplierBO.get(id);

        try {
            put("supplier", dto);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        }
    }

    public void save(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");
        SupplierDTO dto = this.populateDTO(request);

        try {
            if (id == 0) {
                dto.setCreatedBy(request.getLoggedUserId());
                supplierBO.save(dto);
            } else {
                dto.setId(id);
                dto.setModifiedBy(request.getLoggedUserId());
                supplierBO.update(dto);
            }

            if (id == 0) {
                this.setMessage(ActionResult.SUCCESS, "acquisition.supplier.success.save");
            } else {
                this.setMessage(ActionResult.SUCCESS, "acquisition.supplier.success.update");
            }

            put("data", dto.toJSONObject());

            put("full_data", true);
        } catch (JSONException e) {
            this.setMessage(ActionResult.WARNING, ERROR_INVALID_JSON);
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "acquisition.supplier.error.save");
        }
    }

    public void delete(ExtendedRequest request, ExtendedResponse response) {
        Integer id = request.getInteger("id");

        SupplierDTO dto = new SupplierDTO();
        dto.setId(id);

        if (supplierBO.delete(dto)) {
            this.setMessage(ActionResult.SUCCESS, "acquisition.supplier.success.delete");
        } else {
            this.setMessage(ActionResult.WARNING, "acquisition.supplier.error.delete");
        }
    }

    private SupplierDTO populateDTO(ExtendedRequest request) {
        SupplierDTO dto = new SupplierDTO();

        dto.setId(request.getInteger("id"));
        dto.setTrademark(request.getString("trademark"));
        dto.setName(request.getString("supplier_name"));
        dto.setSupplierNumber(request.getString("supplier_number"));
        dto.setVatRegistrationNumber(request.getString("vat_registration_number"));
        dto.setAddress(request.getString("address"));
        dto.setAddressNumber(request.getString("address_number"));
        dto.setComplement(request.getString("complement"));
        dto.setArea(request.getString("area"));
        dto.setCity(request.getString("city"));
        dto.setState(request.getString("state"));
        dto.setCountry(request.getString("country"));
        dto.setZipCode(request.getString("zip_code"));
        dto.setTelephone1(request.getString("telephone_1"));
        dto.setTelephone2(request.getString("telephone_2"));
        dto.setTelephone3(request.getString("telephone_3"));
        dto.setTelephone4(request.getString("telephone_4"));
        dto.setContact1(request.getString("contact_1"));
        dto.setContact2(request.getString("contact_2"));
        dto.setContact3(request.getString("contact_3"));
        dto.setContact4(request.getString("contact_4"));
        dto.setInfo(request.getString("info"));
        dto.setUrl(request.getString("url"));
        dto.setEmail(request.getString("email"));
        return dto;
    }

    @Autowired
    public void setSupplierBO(SupplierBO supplierBO) {
        this.supplierBO = supplierBO;
    }
}

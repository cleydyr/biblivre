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
package biblivre.z3950;

import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordBO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.PagingDTO;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.ActionResult;
import biblivre.core.utils.Constants;
import biblivre.marc.MaterialType;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.marc4j.marc.Record;

public class Handler extends AbstractHandler {
    private BiblioRecordBO biblioRecordBO;
    private Z3950BO z3950BO;

    public void search(ExtendedRequest request, ExtendedResponse response) {

        String searchParameters = request.getString("search_parameters");

        String servers = null;
        String attribute = null;
        String query = null;

        try {
            JSONObject json = new JSONObject(searchParameters);
            servers = json.optString("server");
            attribute = json.optString("attribute");
            query = json.optString("query");
        } catch (JSONException je) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        String[] serverIds = servers.split(",");
        List<Integer> ids = new ArrayList<>();
        for (String serverId : serverIds) {
            try {
                ids.add(Integer.parseInt(serverId.trim()));
            } catch (Exception e) {
            }
        }

        if (ids.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }

        List<Z3950AddressDTO> serverList = z3950BO.list(ids);
        Pair<String, String> search = Pair.of(attribute, query);
        List<Z3950RecordDTO> results = z3950BO.search(serverList, search);

        if (results.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }

        Integer searchId = (Integer) request.getScopedSessionAttribute("z3950_search.last_id");
        if (searchId == null) {
            searchId = 1;
        } else {
            searchId++;
        }

        request.setScopedSessionAttribute("z3950_search." + searchId, results);
        request.setScopedSessionAttribute("z3950_search.last_id", searchId);

        DTOCollection<Z3950RecordDTO> collection = this.paginateResults(results, 1);
        collection.setId(searchId);

        try {
            this.json.putOpt("search", collection.toJSONObject());
        } catch (JSONException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void paginate(ExtendedRequest request, ExtendedResponse response) {

        String searchId = request.getString("search_id");
        if (StringUtils.isBlank(searchId)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }
        Integer page = request.getInteger("page", 1);

        String uuid = "z3950_search." + searchId;
        List<Z3950RecordDTO> results =
                (List<Z3950RecordDTO>) request.getScopedSessionAttribute(uuid);
        if (results == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }
        DTOCollection<Z3950RecordDTO> collection = this.paginateResults(results, page);
        try {
            this.json.putOpt("search", collection.toJSONObject());
        } catch (JSONException e) {
        }
    }

    @SuppressWarnings("unchecked")
    public void open(ExtendedRequest request, ExtendedResponse response) {

        Integer index = request.getInteger("id");
        String searchId = request.getString("search_id");
        if (StringUtils.isBlank(searchId)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }
        String uuid = "z3950_search." + searchId;
        List<Z3950RecordDTO> results =
                (List<Z3950RecordDTO>) request.getScopedSessionAttribute(uuid);
        if (results == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }
        Z3950RecordDTO dto = results.get(index);

        if (dto == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.record_not_found");
            return;
        }

        RecordDTO recordDTO = dto.getRecord();
        Record record = recordDTO.getRecord();

        biblioRecordBO.populateDetails(recordDTO, RecordBO.MARC_INFO);
        recordDTO.setId(index);
        recordDTO.setMaterialType(MaterialType.fromRecord(record));

        try {
            this.json.put("data", recordDTO.toJSONObject());
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_json");
        }
    }

    private DTOCollection<Z3950RecordDTO> paginateResults(List<Z3950RecordDTO> results, int page) {
        Integer recordsPerPage =
                Configurations.getPositiveInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20);
        Integer start = (page - 1) * recordsPerPage;
        PagingDTO paging = new PagingDTO(results.size(), recordsPerPage, start);
        DTOCollection<Z3950RecordDTO> collection = new DTOCollection<>();

        List<Z3950RecordDTO> sublist =
                results.subList(start, Math.min(start + recordsPerPage, results.size()));

        int autoGeneratedId = start;
        for (Z3950RecordDTO z3950 : sublist) {
            z3950.setAutogenId(autoGeneratedId++);

            biblioRecordBO.populateDetails(z3950.getRecord(), RecordBO.MARC_INFO);
        }

        collection.addAll(sublist);
        collection.setPaging(paging);
        return collection;
    }

    public void setBiblioRecordBO(BiblioRecordBO biblioRecordBO) {
        this.biblioRecordBO = biblioRecordBO;
    }

    public void setZ3950BO(Z3950BO z3950bo) {
        z3950BO = z3950bo;
    }
}

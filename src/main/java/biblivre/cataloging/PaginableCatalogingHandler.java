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
package biblivre.cataloging;

import biblivre.administration.indexing.IndexingGroupDTO;
import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.bibliographic.PaginableRecordBO;
import biblivre.cataloging.enums.AutocompleteType;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

public abstract class PaginableCatalogingHandler extends CatalogingHandler {

    private PaginableRecordBO paginableRecordBO;

    public PaginableCatalogingHandler(
            PaginableRecordBO recordBO, MaterialType defaultMaterialType) {
        super(recordBO, defaultMaterialType);

        paginableRecordBO = (PaginableRecordBO) recordBO;
    }

    public void search(ExtendedRequest request, ExtendedResponse response) {
        String searchParameters = request.getString("search_parameters");

        SearchQueryDTO searchQuery = new SearchQueryDTO(searchParameters);

        SearchDTO search = paginableRecordBO.search(searchQuery, request.getAuthorizationPoints());

        if (search.size() == 0) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
        }

        if (CollectionUtils.isEmpty(search)) {
            return;
        }

        List<IndexingGroupDTO> groups = IndexingGroups.getGroups(paginableRecordBO.getRecordType());

        this.json.put("search", search.toJSONObject());

        for (IndexingGroupDTO group : groups) {
            this.json.accumulate("indexing_groups", group.toJSONObject());
        }
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        SearchDTO search = HttpRequestSearchHelper.paginate(request, paginableRecordBO);

        if (search == null) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");

            return;
        }

        paginableRecordBO.paginateSearch(search, request.getAuthorizationPoints());

        if (search.size() == 0) {
            paginableRecordBO.search(search);
        }

        if (search.size() == 0) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
        }

        if (CollectionUtils.isEmpty(search)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
            return;
        }

        this.json.put("search", search.toJSONObject());

        List<IndexingGroupDTO> groups = IndexingGroups.getGroups(paginableRecordBO.getRecordType());

        for (IndexingGroupDTO group : groups) {
            this.json.accumulate("indexing_groups", group.toJSONObject());
        }
    }

    public void convert(ExtendedRequest request, ExtendedResponse response) {
        String data = request.getString("data");

        RecordConvertion from = request.getEnum(RecordConvertion.class, "from");

        MaterialType materialType =
                request.getEnum(MaterialType.class, "material_type", this.defaultMaterialType);

        Integer id = request.getInteger("id");

        boolean isNew = id == 0;

        RecordDTO dto = this.createRecordDTO(request);

        Record record = null;

        try {
            MarcReader marcReader =
                    from.getReader(data, materialType, RecordStatus.fromNewStatus(isNew));

            record = marcReader.next();

            dto.setRecord(record);

            dto.setId(id);

            dto.setMaterialType(materialType);
        } catch (Exception e) {
            this.setMessage(ActionResult.WARNING, "error.invalid_parameters");
            return;
        }

        this.json.put("data", dto.toJSONObject());
    }

    public void itemCount(ExtendedRequest request, ExtendedResponse response) {
        RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");

        if (recordDatabase == null) {
            ValidationException ex = new ValidationException("cataloging.error.invalid_database");

            this.setMessage(ex);

            return;
        }

        SearchQueryDTO query = new SearchQueryDTO(recordDatabase);

        SearchDTO searchDTO = new SearchDTO(paginableRecordBO.getRecordType());

        searchDTO.setQuery(query);

        int count = paginableRecordBO.count(searchDTO, request.getAuthorizationPoints());

        this.json.put("count", count);
    }

    public void moveRecords(ExtendedRequest request, ExtendedResponse response) {
        RecordDatabase recordDatabase = request.getEnum(RecordDatabase.class, "database");

        if (recordDatabase == null) {
            ValidationException ex = new ValidationException("cataloging.error.invalid_database");
            this.setMessage(ex);
            return;
        }

        String[] idArray = request.getString("id_list").split(",");

        Set<Integer> ids =
                Arrays.stream(idArray).map(id -> Integer.parseInt(id)).collect(Collectors.toSet());

        int loggedUserId = request.getLoggedUserId();

        boolean success =
                paginableRecordBO.moveRecords(
                        ids, recordDatabase, loggedUserId, request.getAuthorizationPoints());

        if (success) {
            this.setMessage(ActionResult.SUCCESS, "cataloging.record.success.move");
        } else {
            this.setMessage(ActionResult.WARNING, "cataloging.record.error.move");
        }
    }

    public void exportRecords(ExtendedRequest request, ExtendedResponse response) {

        String exportId = UUID.randomUUID().toString();

        String idList = request.getString("id_list");

        request.setScopedSessionAttribute(exportId, idList);

        this.json.put("uuid", exportId);
    }

    public void downloadExport(ExtendedRequest request, ExtendedResponse response) {

        String exportId = request.getString("id");

        String idList = (String) request.getScopedSessionAttribute(exportId);

        String[] idArray = idList.split(",");

        Set<Integer> ids =
                Arrays.stream(idArray).map(id -> Integer.parseInt(id)).collect(Collectors.toSet());

        AuthorizationPoints authorizationPoints = request.getAuthorizationPoints();

        final DiskFile exportFile = paginableRecordBO.createExportFile(ids, authorizationPoints);

        this.setFile(exportFile);

        this.setCallback(exportFile::delete);
    }

    public void autocomplete(ExtendedRequest request, ExtendedResponse response) {
        String query = request.getString("q");

        if (StringUtils.isBlank(query)) {
            return;
        }

        String datafield = request.getString("datafield", "000");

        String subfield = request.getString("subfield", "a");

        AutocompleteType type =
                request.getEnum(AutocompleteType.class, "type", AutocompleteType.DISABLED);

        switch (type) {
            case FIXED_TABLE:
            case FIXED_TABLE_WITH_PREVIOUS_VALUES:
            case PREVIOUS_VALUES:
                AutocompleteType.GetSuggestionsParameters parameterObject =
                        AutocompleteType.GetSuggestionsParameters.builder()
                                .withDatafield(datafield)
                                .withSubfield(subfield)
                                .withQuery(query)
                                .withRecordBO(paginableRecordBO)
                                .build();

                for (String term : type.getSuggestions(parameterObject)) {
                    this.json.append("data", term);
                }

                break;
            case BIBLIO:
            case AUTHORITIES:
            case VOCABULARY:
                DTOCollection<AutocompleteDTO> autocompletion =
                        type.getAutocompletion(paginableRecordBO, query);

                this.json.putOpt("data", autocompletion.toJSONObject());

                break;

            default:
        }
    }

    public void addAttachment(ExtendedRequest request, ExtendedResponse response) {
        Integer recordId = request.getInteger("id");

        String uri = request.getString("uri");

        String description = request.getString("description");

        Integer userId = request.getLoggedUserId();

        paginableRecordBO.addAttachment(recordId, uri, description, userId);
    }

    public void removeAttachment(ExtendedRequest request, ExtendedResponse response) {
        Integer recordId = request.getInteger("id");

        String uri = request.getString("uri");

        String description = request.getString("description");

        Integer userId = request.getLoggedUserId();

        paginableRecordBO.removeAttachment(recordId, uri, description, userId);
    }

    public void listBriefFormats(ExtendedRequest request, ExtendedResponse response) {

        List<BriefTabFieldFormatDTO> formats =
                Fields.getBriefFormats(paginableRecordBO.getRecordType());

        DTOCollection<BriefTabFieldFormatDTO> list = new DTOCollection<>();

        list.addAll(formats);

        this.json.put("data", list.toJSONObject());
    }
}

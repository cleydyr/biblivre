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

import biblivre.administration.indexing.IndexingGroupBO;
import biblivre.administration.indexing.IndexingGroupDTO;
import biblivre.cataloging.bibliographic.PaginableRecordBO;
import biblivre.cataloging.enums.AutocompleteType;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.enums.ActionResult;
import biblivre.core.exceptions.ValidationException;
import biblivre.core.file.DiskFile;
import biblivre.marc.MarcDataReader;
import biblivre.marc.MaterialType;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public abstract class PaginableCatalogingHandler extends CatalogingHandler {
    private static final Logger logger = LoggerFactory.getLogger(PaginableCatalogingHandler.class);
    protected final PaginableRecordBO paginableRecordBO;

    protected Map<RecordType, PaginableRecordBO> paginableRecordBOs;

    private IndexingGroupBO indexingGroupBO;

    private ConfigurationBO configurationBO;

    protected TabFieldsBO tabFieldsBO;

    protected SearchResultsExcelExporter searchResultsExcelExporter;

    public PaginableCatalogingHandler(
            PaginableRecordBO paginableRecordBO,
            MaterialType defaultMaterialType,
            SearchResultsExcelExporter searchResultsExcelExporter) {
        super(paginableRecordBO, defaultMaterialType);

        this.paginableRecordBO = paginableRecordBO;
        this.searchResultsExcelExporter = searchResultsExcelExporter;
    }

    public void search(ExtendedRequest request, ExtendedResponse response) {
        String searchParameters = request.getString("search_parameters");

        SearchQueryDTO searchQuery = new SearchQueryDTO(searchParameters);

        SearchDTO search = paginableRecordBO.search(searchQuery, request.getAuthorizationPoints());

        if (search.isEmpty()) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");
        }

        if (CollectionUtils.isEmpty(search)) {
            return;
        }

        List<IndexingGroupDTO> groups =
                indexingGroupBO.getGroups(paginableRecordBO.getRecordType());

        put("search", search.toJSONObject());

        for (IndexingGroupDTO group : groups) {
            accumulate("indexing_groups", group.toJSONObject());
        }
    }

    public void paginate(ExtendedRequest request, ExtendedResponse response) {
        Integer defaultSortableGroupId =
                indexingGroupBO.getDefaultSortableGroupId(paginableRecordBO.getRecordType());

        SearchDTO search =
                HttpRequestSearchHelper.paginate(
                        request, paginableRecordBO, defaultSortableGroupId);

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

        put("search", search.toJSONObject());

        List<IndexingGroupDTO> groups =
                indexingGroupBO.getGroups(paginableRecordBO.getRecordType());

        for (IndexingGroupDTO group : groups) {
            accumulate("indexing_groups", group.toJSONObject());
        }
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

        put("count", count);
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
                Arrays.stream(idArray).map(Integer::parseInt).collect(Collectors.toSet());

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

        put("uuid", exportId);
    }

    public void downloadExport(ExtendedRequest request, ExtendedResponse response) {

        String exportId = request.getString("id");

        String idList = (String) request.getScopedSessionAttribute(exportId);

        String[] idArray = idList.split(",");

        Set<Integer> ids =
                Arrays.stream(idArray).map(Integer::parseInt).collect(Collectors.toSet());

        AuthorizationPoints authorizationPoints = request.getAuthorizationPoints();

        final DiskFile exportFile = paginableRecordBO.createExportFile(ids, authorizationPoints);

        this.setFile(exportFile);

        this.setCallback(exportFile::delete);
    }

    public void exportSearchExcel(ExtendedRequest request, ExtendedResponse response) {
        if (!configurationBO.isSearchExcelExportEnabled()) {
            this.setMessage(ActionResult.WARNING, "error.no_permission");

            return;
        }

        String searchParameters = request.getString("search_parameters");

        if (StringUtils.isBlank(searchParameters)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.invalid_search_parameters");

            return;
        }

        Integer sortOpt = request.getInteger("sort", null);
        int sort =
                sortOpt != null
                        ? sortOpt
                        : indexingGroupBO.getDefaultSortableGroupId(
                                paginableRecordBO.getRecordType());
        int indexingGroup = request.getInteger("indexing_group", 0);

        String exportId = UUID.randomUUID().toString();

        JSONObject payload = new JSONObject();
        payload.put("search_parameters", searchParameters);
        payload.put("sort", sort);
        payload.put("indexing_group", indexingGroup);

        request.setScopedSessionAttribute(exportId, payload.toString());

        put("uuid", exportId);
    }

    public void downloadSearchExcel(ExtendedRequest request, ExtendedResponse response) {
        if (!configurationBO.isSearchExcelExportEnabled()) {
            this.setMessage(ActionResult.WARNING, "error.no_permission");

            return;
        }

        String exportId = request.getString("id");

        String raw = (String) request.getScopedSessionAttribute(exportId);

        if (StringUtils.isBlank(raw)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");

            return;
        }

        JSONObject payload = new JSONObject(raw);

        String searchParameters = payload.getString("search_parameters");

        int sort = payload.getInt("sort");

        int indexingGroup = payload.optInt("indexing_group", 0);

        SearchQueryDTO searchQuery = new SearchQueryDTO(searchParameters);

        AuthorizationPoints authorizationPoints = request.getAuthorizationPoints();

        SearchDTO search =
                paginableRecordBO.search(searchQuery, authorizationPoints, sort, indexingGroup);

        if (CollectionUtils.isEmpty(search)) {
            this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");

            return;
        }

        long pageCount = search.getPaging().getPageCount();

        for (int p = 2; p <= pageCount; p++) {
            search.getPaging().setPage(p);

            paginableRecordBO.paginateSearch(search, authorizationPoints);
        }

        List<RecordDTO> rows = new ArrayList<>(search);

        try {
            DiskFile file = searchResultsExcelExporter.export(rows, request.getLanguage());

            if (file == null) {
                this.setMessage(ActionResult.WARNING, "cataloging.error.no_records_found");

                return;
            }

            this.setFile(file);

            this.setCallback(file::delete);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);

            this.setMessage(ActionResult.ERROR, "error.runtime_error");
        }
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
            case FIXED_TABLE, FIXED_TABLE_WITH_PREVIOUS_VALUES, PREVIOUS_VALUES -> {
                AutocompleteType.GetSuggestionsParameters parameterObject =
                        AutocompleteType.GetSuggestionsParameters.builder()
                                .withDatafield(datafield)
                                .withSubfield(subfield)
                                .withQuery(query)
                                .withRecordBO(paginableRecordBO)
                                .build();
                for (String term : type.getSuggestions(parameterObject)) {
                    append("data", term);
                }
            }
            case BIBLIO, AUTHORITIES, VOCABULARY -> {
                RecordType recordType = RecordType.fromString(type.toString());
                PaginableRecordBO autocompleteRecordBO = paginableRecordBOs.get(recordType);
                DTOCollection<AutocompleteDTO> autocompletion =
                        type.getAutocompletion(
                                autocompleteRecordBO,
                                query,
                                tabFieldsBO.getAutocompleteSubFields(recordType));
                putOpt("data", autocompletion.toJSONObject());
            }
            default -> {}
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
                tabFieldsBO.getBriefFormats(paginableRecordBO.getRecordType());

        DTOCollection<BriefTabFieldFormatDTO> list = new DTOCollection<>();

        list.addAll(formats);

        put("data", list.toJSONObject());
    }

    protected void populateFields(RecordDTO dto, JSONObject data) {
        MarcDataReader marcDataReader = new MarcDataReader(dto.getRecord());

        List<BriefTabFieldDTO> fieldList =
                marcDataReader.getFieldList(tabFieldsBO.getBriefFormats(dto.getRecordType()));

        DTOCollection<AbstractDTO> collection = new DTOCollection<>();

        collection.addAll(fieldList);

        dto.addExtraData("fields", collection);

        dto.populateExtraData(data);
    }

    @Autowired
    public void setPaginableRecordBOs(Map<String, PaginableRecordBO> paginableRecordBOs) {
        this.paginableRecordBOs =
                paginableRecordBOs.entrySet().stream()
                        .collect(
                                Collectors.toMap(
                                        entry -> {
                                            String recordTypeName =
                                                    entry.getKey().replaceFirst("RecordBO", "");

                                            return RecordType.fromString(recordTypeName);
                                        },
                                        Map.Entry::getValue));
    }

    @Autowired
    public void setIndexingGroupBO(IndexingGroupBO indexingGroupBO) {
        this.indexingGroupBO = indexingGroupBO;
    }

    @Autowired
    public void setConfigurationBO(ConfigurationBO configurationBO) {
        this.configurationBO = configurationBO;
    }

    @Autowired
    public void setFieldsBO(TabFieldsBO tabFieldsBO) {
        this.tabFieldsBO = tabFieldsBO;
    }
}

package biblivre.cataloging.bibliographic;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.RecordBO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.holding.HoldingBO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.PagingDTO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.Configurations;
import biblivre.core.enums.SearchMode;
import biblivre.core.utils.Constants;
import java.util.List;
import java.util.Map;

public abstract class PaginableRecordBO extends RecordBO {
    protected HoldingBO holdingBO;

    public boolean paginateSearch(SearchDTO search) {
        if (search.getQuery().isHoldingSearch()) {
            return holdingBO.paginateHoldingSearch(search);
        }

        Map<Integer, Integer> groupCount = this.recordDAO.countSearchResults(search);
        Integer count = groupCount.get(search.getIndexingGroup());

        if (count == null || count == 0) {
            return false;
        }

        List<RecordDTO> list = this.recordDAO.getSearchResults(search);

        search.getPaging().setRecordCount(count);
        search.setIndexingGroupCount(groupCount);

        for (RecordDTO rdto : list) {
            this.populateDetails(rdto, RecordBO.MARC_INFO | RecordBO.HOLDING_INFO);
            search.add(rdto);
        }

        return true;
    }

    public SearchDTO search(SearchQueryDTO searchQuery, AuthorizationPoints authorizationPoints) {
        if (searchQuery.getDatabase() == RecordDatabase.PRIVATE) {
            authorize("cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        SearchDTO search = new SearchDTO(getRecordType());

        PagingDTO paging = _newConfiguredPagingInstance();

        search.setPaging(paging);

        search.setQuery(searchQuery);

        search.setSort(IndexingGroups.getDefaultSortableGroupId(getRecordType()));

        search(search);

        paging.endTimer();

        return search;
    }

    public boolean paginateSearch(SearchDTO search, AuthorizationPoints authorizationPoints) {
        if (search.getQuery().getDatabase() == RecordDatabase.PRIVATE) {
            this.authorize(
                    "cataloging.bibliographic", "private_database_access", authorizationPoints);
        }

        return paginateSearch(search);
    }

    public boolean search(SearchDTO search) {
        SearchMode searchMode = search.getSearchMode();

        boolean isNewSearch = (search.getId() == null);

        if (isNewSearch) {
            if (!this.searchDAO.createSearch(search)) {
                return false;
            }
        }

        switch (searchMode) {
            case SIMPLE:
                if (!this.searchDAO.populateSimpleSearch(search, !isNewSearch)) {
                    return false;
                }

                break;

            case ADVANCED:
                if (!this.searchDAO.populateAdvancedSearch(search, !isNewSearch)) {
                    return false;
                }

                break;

            case LIST_ALL:
                break;
        }

        return this.paginateSearch(search);
    }

    private PagingDTO _newConfiguredPagingInstance() {
        PagingDTO paging = new PagingDTO();

        paging.setRecordsPerPage(
                Configurations.getPositiveInt(Constants.CONFIG_SEARCH_RESULTS_PER_PAGE, 20));

        paging.setRecordLimit(
                Configurations.getPositiveInt(Constants.CONFIG_SEARCH_RESULT_LIMIT, 2000));

        paging.setPage(1);

        return paging;
    }

    public void setHoldingBO(HoldingBO holdingBO) {
        this.holdingBO = holdingBO;
    }
}

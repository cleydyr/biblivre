package biblivre.cataloging;

import biblivre.cataloging.bibliographic.PaginableRecordBO;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.ExtendedRequest;

public class HttpRequestSearchHelper {
    public static SearchDTO paginate(
            ExtendedRequest request,
            PaginableRecordBO paginableRecordBO,
            int defaultSortableGroupId) {
        Integer searchId = request.getInteger("search_id", null);

        SearchDTO search = paginableRecordBO.getSearch(searchId);

        if (search == null) {
            return SearchDTO.EMPTY;
        }

        Integer sort = request.getInteger("sort", defaultSortableGroupId);

        Integer page = request.getInteger("page", 1);

        Integer indexingGroup = request.getInteger("indexing_group", 0);

        search.getPaging().setPage(page);
        search.setSort(sort);
        search.setIndexingGroup(indexingGroup);

        return search;
    }
}

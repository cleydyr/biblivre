package biblivre.cataloging;

import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.ExtendedRequest;

public class HttpRequestSearchHelper {
    public static SearchDTO paginate(ExtendedRequest request, RecordBO recordBO) {
        Integer searchId = request.getInteger("search_id", null);

        SearchDTO search = recordBO.getSearch(searchId);

        if (search == null) {
            return null;
        }

        Integer defaultSortableGroupId =
                IndexingGroups.getDefaultSortableGroupId(
                        request.getSchema(), recordBO.getRecordType());

        Integer sort = request.getInteger("sort", defaultSortableGroupId);

        Integer page = request.getInteger("page", 1);

        Integer indexingGroup = request.getInteger("indexing_group", 0);

        search.getPaging().setPage(page);
        search.setSort(sort);
        search.setIndexingGroup(indexingGroup);

        return search;
    }
}

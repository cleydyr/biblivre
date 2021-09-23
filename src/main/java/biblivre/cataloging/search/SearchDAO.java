package biblivre.cataloging.search;

import biblivre.cataloging.enums.RecordType;

public interface SearchDAO {

    SearchDTO getSearch(Integer searchId, RecordType recordType);

    boolean createSearch(SearchDTO search);

    boolean populateSimpleSearch(SearchDTO search, boolean deleteOldResults);

    boolean populateAdvancedSearch(SearchDTO search, boolean deleteOld);
}

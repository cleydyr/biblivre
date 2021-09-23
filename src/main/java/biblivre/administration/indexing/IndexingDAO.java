package biblivre.administration.indexing;

import biblivre.cataloging.AutocompleteDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import java.util.List;

public interface IndexingDAO {

    Integer countIndexed(RecordType recordType);

    void clearIndexes(RecordType recordType);

    void startIndexing();

    void insertIndexes(RecordType recordType, List<IndexingDTO> indexes);

    void insertSortIndexes(RecordType recordType, List<IndexingDTO> sortIndexes);

    void insertAutocompleteIndexes(
            RecordType recordType, List<AutocompleteDTO> autocompleteIndexes);

    void reindexAutocompleteFixedTable(
            RecordType recordType, String datafield, String subfield, List<String> phrases);

    boolean deleteIndexes(RecordType recordType, RecordDTO dto);

    void reindexDatabase(RecordType recordType);

    List<String> searchExactTerms(RecordType recordType, int indexingGroupId, List<String> terms);
}

package biblivre.cataloging;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.core.DTOCollection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RecordDAO {

    boolean save(RecordDTO dto);

    boolean update(RecordDTO dto);

    boolean listContainsPrivateRecord(Set<Integer> ids, RecordType recordType);

    boolean moveRecords(
            Set<Integer> ids, int modifiedBy, RecordDatabase database, RecordType recordType);

    boolean delete(RecordDTO dto);

    Integer count(SearchDTO search);

    Map<Integer, RecordDTO> map(Set<Integer> ids, RecordType recordType);

    List<RecordDTO> list(int offset, int limit, RecordType recordType);

    List<RecordDTO> list(int offset, int limit, RecordDatabase database, RecordType recordType);

    List<RecordDTO> listByLetter(char letter, int order, RecordType recordType);

    Map<Integer, Integer> countSearchResults(SearchDTO search);

    List<RecordDTO> getSearchResults(SearchDTO search);

    List<String> phraseAutocomplete(
            String datafield,
            String subfield,
            String[] terms,
            int limit,
            boolean startsWith,
            RecordType recordType);

    DTOCollection<AutocompleteDTO> recordAutocomplete(
            String datafield,
            String subfield,
            String[] terms,
            int limit,
            boolean startsWith,
            RecordType recordType);
}

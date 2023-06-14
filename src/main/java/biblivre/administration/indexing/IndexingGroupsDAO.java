package biblivre.administration.indexing;

import biblivre.cataloging.enums.RecordType;
import java.util.List;

public interface IndexingGroupsDAO {
    List<IndexingGroupDTO> list(RecordType recordType);
}

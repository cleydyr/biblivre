package biblivre.administration.indexing;

import java.util.List;

import biblivre.cataloging.enums.RecordType;

public interface IndexingGroupsDAO {
	public List<IndexingGroupDTO> list(RecordType recordType);
}

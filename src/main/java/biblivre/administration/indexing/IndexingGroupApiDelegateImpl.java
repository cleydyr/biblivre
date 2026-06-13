package biblivre.administration.indexing;

import biblivre.cataloging.enums.RecordType;
import biblivre.reports.generated.api.IndexingGroupsApiDelegate;
import biblivre.reports.generated.api.model.RestIndexingGroup;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class IndexingGroupApiDelegateImpl implements IndexingGroupsApiDelegate {
    private IndexingGroupBO indexingGroupBO;

    @Override
    public ResponseEntity<List<RestIndexingGroup>> getIndexingGroups(String recordType) {
        RecordType type = RecordType.fromString(recordType);

        if (type == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                indexingGroupBO.getGroups(type).stream().map(this::toRestIndexingGroup).toList(),
                HttpStatus.OK);
    }

    private RestIndexingGroup toRestIndexingGroup(IndexingGroupDTO group) {
        RestIndexingGroup restIndexingGroup = new RestIndexingGroup();
        restIndexingGroup.setId(group.getId());
        restIndexingGroup.setTranslationKey(group.getTranslationKey());
        restIndexingGroup.setDatafields(group.getDatafields());
        restIndexingGroup.setSortable(group.isSortable());
        restIndexingGroup.setDefaultSort(group.isDefaultSort());
        return restIndexingGroup;
    }

    @Autowired
    public void setIndexingGroupBO(IndexingGroupBO indexingGroupBO) {
        this.indexingGroupBO = indexingGroupBO;
    }
}

package biblivre.marc;

import org.json.JSONObject;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

public class JsonMarcReader implements MarcReader {
    private final String json;
    private final MaterialType materialType;
    private final RecordStatus recordStatus;

    private boolean hasNext = true;

    public JsonMarcReader(String json, MaterialType materialType, RecordStatus recordStatus) {
        super();

        this.json = json;
        this.materialType = materialType;
        this.recordStatus = recordStatus;
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Record next() {
        hasNext = false;

        return MarcUtils.jsonToRecord(new JSONObject(this.json), materialType, recordStatus);
    }
}

package biblivre.marc;

import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

public class HumanReadableMarcReader implements MarcReader {
    private final String humanReadableMarc;
    private final MaterialType materialType;
    private final RecordStatus recordStatus;

    private boolean hasNext = true;

    public HumanReadableMarcReader(
            String humanReadableMarc, MaterialType materialType, RecordStatus recordStatus) {
        super();
        this.humanReadableMarc = humanReadableMarc;
        this.materialType = materialType;
        this.recordStatus = recordStatus;
    }

    public HumanReadableMarcReader(String humanReadableMarc, RecordStatus recordStatus) {
        this(humanReadableMarc, null, recordStatus);
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Record next() {
        hasNext = false;

        return MarcUtils.marcToRecord(humanReadableMarc, materialType, recordStatus);
    }
}

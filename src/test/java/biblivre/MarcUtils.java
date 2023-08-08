package biblivre;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.marc.HumanReadableMarcReader;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import org.jetbrains.annotations.NotNull;

public class MarcUtils {
    @NotNull
    public static BiblioRecordDTO getBiblioRecordDTOFromHumanReadableMarc(
            String humanReadableMarc) {
        HumanReadableMarcReader humanReadableMarcReader =
                new HumanReadableMarcReader(humanReadableMarc, MaterialType.BOOK, RecordStatus.NEW);

        BiblioRecordDTO record = new BiblioRecordDTO();

        record.setRecord(humanReadableMarcReader.next());

        return record;
    }
}

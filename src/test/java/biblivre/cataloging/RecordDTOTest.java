package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;

import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.Record;

class RecordDTOTest {

    @Test
    void setIdReplacesMismatchedControlNumber() {
        String humanReadableMarc =
                """
                000 00000cam a2200000 a 4500
                001 0009999
                245 10|aTest holding with wrong control number
                """;

        Record record =
                MarcUtils.marcToRecord(humanReadableMarc, MaterialType.BOOK, RecordStatus.NEW);

        RecordDTO dto = new RecordDTO();
        dto.setRecord(record);

        assertEquals(9999, dto.getId());

        dto.setId(123);

        assertEquals(123, dto.getId());
        assertEquals("0000123", dto.getRecord().getControlNumber());
        assertEquals(1, dto.getRecord().getVariableFields("001").size());
    }
}

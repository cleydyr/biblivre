package biblivre.marc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

class MarcUtilsTest {

    @Test
    void marcToRecord() {
        String humanReadableMarc =
                """
                000 00000cam a2200000 a 4500
                001 0000001
                245 10|aFifty years of television :|ba guide to series and pilots, 1937-1988 /|cVincent Terrace.
                """;

        Record record =
                MarcUtils.marcToRecord(humanReadableMarc, MaterialType.BOOK, RecordStatus.NEW);

        assertEquals(0, record.getLeader().getRecordLength());
        DataField dataField245 = record.getDataFields().get(0);
        assertEquals('1', dataField245.getIndicator1());
        assertEquals('0', dataField245.getIndicator2());
        assertEquals(3, dataField245.getSubfields().size());
        assertEquals(1, dataField245.getSubfields('a').size());
        assertEquals(1, dataField245.getSubfields('b').size());
        assertEquals(1, dataField245.getSubfields('c').size());
        assertEquals("Fifty years of television :", dataField245.getSubfield('a').getData());
        assertEquals(
                "a guide to series and pilots, 1937-1988 /",
                dataField245.getSubfield('b').getData());
        assertEquals("Vincent Terrace.", dataField245.getSubfield('c').getData());
    }

    @Test
    void detectSplitter() {
        String humanReadableMarc =
                """
                000 00000cam a2200000 a 4500
                001 0000001
                245 10|aFifty years of television :|ba guide to series and pilots, 1937-1988 /|cVincent Terrace.
                """;

        assertEquals('|', MarcUtils.detectSplitter(humanReadableMarc));
    }

    @Test
    void recordLength() {
        String humanReadableMarc =
                """
                000 00092cam a2200000 a 4500
                001 0000001
                245 10|aFifty years of television :|ba guide to series and pilots, 1937-1988 /|cVincent Terrace.
                """;

        Record record =
                MarcUtils.marcToRecord(humanReadableMarc, MaterialType.BOOK, RecordStatus.NEW);

        assertEquals(92, record.getLeader().getRecordLength());
    }
}

package biblivre.update.v6_0_0$8_0_1$alpha;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

class UpdateTest {

    @Test
    void needsControlNumberFixWhen001DoesNotMatchId() {
        Record record =
                MarcUtils.marcToRecord(
                        """
                        000 00000cam a2200000 a 4500
                        001 0009999
                        245 10|aMismatched control number
                        """,
                        MaterialType.HOLDINGS,
                        RecordStatus.NEW);

        assertTrue(Update.needsControlNumberFix(record, 123));
    }

    @Test
    void doesNotNeedControlNumberFixWhen001MatchesId() {
        Record record =
                MarcUtils.marcToRecord(
                        """
                        000 00000cam a2200000 a 4500
                        001 0000123
                        245 10|aMatching control number
                        """,
                        MaterialType.HOLDINGS,
                        RecordStatus.NEW);

        assertFalse(Update.needsControlNumberFix(record, 123));
    }

    @Test
    void needsControlNumberFixWhen001IsMissing() {
        Record record = MarcFactory.newInstance().newRecord();

        assertTrue(Update.needsControlNumberFix(record, 123));
    }
}

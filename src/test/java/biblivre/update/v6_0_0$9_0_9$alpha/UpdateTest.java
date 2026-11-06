package biblivre.update.v6_0_0$9_0_9$alpha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.utils.Constants;
import biblivre.marc.Iso2709;
import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Test;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

class UpdateTest {

    private static final MarcFactory FACTORY = MarcFactory.newInstance();

    @Test
    void repairsRecordWithCappedFieldLength() {
        String contents = "Capítulo ".repeat(2500);

        RecordDTO dto = Update.repair(writeWithMarc4j(contents), RecordType.BIBLIO, 42);

        assertNotNull(dto);
        assertInstanceOf(BiblioRecordDTO.class, dto);
        assertEquals(42, dto.getId());
        assertFalse(Iso2709.needsLenientParse(dto.getIso2709()));

        StringBuilder rebuilt = new StringBuilder();

        for (var field : MarcUtils.iso2709ToRecord(dto.getIso2709()).getVariableFields("505")) {
            rebuilt.append(((DataField) field).getSubfield('a').getData());
        }

        assertEquals(contents, rebuilt.toString());
    }

    @Test
    void leavesWellFormedRecordAlone() {
        assertNull(Update.repair(writeWithMarc4j("Capítulo 1"), RecordType.BIBLIO, 42));
    }

    @Test
    void skipsUnreadableRecord() {
        byte[] garbage = "not a marc record".getBytes(Constants.DEFAULT_CHARSET);

        assertNull(Update.repair(garbage, RecordType.BIBLIO, 42));
    }

    private static byte[] writeWithMarc4j(String contents) {
        Record record =
                FACTORY.newRecord(MarcUtils.createBasicLeader(MaterialType.BOOK, RecordStatus.NEW));

        record.addVariableField(FACTORY.newControlField("001", "0000042"));

        DataField field = FACTORY.newDataField("505", '0', ' ');
        field.addSubfield(FACTORY.newSubfield('a', contents));
        record.addVariableField(field);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        MarcStreamWriter writer = new MarcStreamWriter(os, Constants.DEFAULT_CHARSET_NAME);
        writer.write(record);
        writer.close();

        return os.toByteArray();
    }
}

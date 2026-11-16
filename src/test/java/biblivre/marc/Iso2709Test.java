package biblivre.marc;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.core.utils.Constants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.marc4j.MarcException;
import org.marc4j.MarcPermissiveStreamReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

class Iso2709Test {

    private static final MarcFactory FACTORY = MarcFactory.newInstance();

    @Test
    void writeSplitsOversizedFieldIntoSameTagFields() {
        String contents = words("Capítulo ", 2500);

        byte[] iso2709 = Iso2709.write(recordWith(field("505", '0', ' ', 'a', contents)));

        Record record = readStrict(iso2709);

        List<DataField> fields = dataFields(record, "505");

        assertTrue(fields.size() > 1);

        for (DataField field : fields) {
            assertEquals('0', field.getIndicator1());
            assertEquals(1, field.getSubfields('a').size());
        }

        assertEquals(contents, concatenate(fields, 'a'));
        assertFieldLengthsFit(iso2709);
    }

    @Test
    void writeSplitsBetweenSubfieldsWhenTheyFit() {
        DataField contents = FACTORY.newDataField("505", '0', '0');

        for (int i = 0; i < 15; i++) {
            contents.addSubfield(FACTORY.newSubfield('t', "t".repeat(990)));
            contents.addSubfield(FACTORY.newSubfield('r', "r".repeat(10)));
        }

        Record record = readStrict(Iso2709.write(recordWith(contents)));

        List<DataField> fields = dataFields(record, "505");

        assertEquals(2, fields.size());

        for (DataField field : fields) {
            for (Subfield subfield : field.getSubfields()) {
                assertEquals(subfield.getCode() == 't' ? 990 : 10, subfield.getData().length());
            }
        }

        assertEquals(30, fields.stream().mapToInt(field -> field.getSubfields().size()).sum());
    }

    @Test
    void writeReplacesEmbeddedSeparators() {
        byte[] iso2709 =
                Iso2709.write(recordWith(field("500", ' ', ' ', 'a', "one\u001Etwo\u001Dthree")));

        Record record = readStrict(iso2709);

        assertEquals("one two three", dataFields(record, "500").get(0).getSubfield('a').getData());
    }

    @Test
    void writeLeavesShortRecordsAsMarc4jWritesThem() {
        Record record = recordWith(field("245", '1', '0', 'a', "Título"));

        assertArrayEquals(writeWithMarc4j(record), Iso2709.write(record));
    }

    @Test
    void marc4jWritesOversizedFieldsWithCappedLength() {
        byte[] iso2709 = corruptedRecord(words("Capítulo ", 2500));

        int baseAddress = Integer.parseInt(new String(iso2709, 12, 5, Constants.DEFAULT_CHARSET));
        String entry505 = new String(iso2709, 24 + 2 * 12, 12, Constants.DEFAULT_CHARSET);

        assertEquals("5059999", entry505.substring(0, 7));
        assertTrue(baseAddress > 0);
        assertTrue(Iso2709.needsLenientParse(iso2709));
    }

    @Test
    void marc4jCannotReadCappedFieldWithEmbeddedFieldTerminator() {
        byte[] iso2709 = corruptedRecord(words("Parte ", 2000) + "\u001E" + words("Fim ", 1000));

        assertThrows(MarcException.class, () -> readPermissive(iso2709));
    }

    @Test
    void iso2709ToRecordReadsCappedDirectoryFromOffsets() {
        String contents = words("Capítulo ", 2500);

        Record record = MarcUtils.iso2709ToRecord(corruptedRecord(contents));

        assertNotNull(record);
        assertEquals("0000042", record.getControlNumber());
        assertEquals(contents, dataFields(record, "505").get(0).getSubfield('a').getData());
        assertEquals("Título", dataFields(record, "245").get(0).getSubfield('a').getData());
    }

    @Test
    void iso2709ToRecordReplacesEmbeddedSeparatorsInCappedField() {
        String contents = words("Parte ", 2000) + "\u001E" + words("Fim ", 1000) + "\u001D.";

        Record record = MarcUtils.iso2709ToRecord(corruptedRecord(contents));

        String expected = contents.replace('\u001E', ' ').replace('\u001D', ' ');

        assertEquals(expected, dataFields(record, "505").get(0).getSubfield('a').getData());
        assertEquals("Título", dataFields(record, "245").get(0).getSubfield('a').getData());
    }

    @Test
    void iso2709ToRecordThrowsInsteadOfReturningNull() {
        byte[] garbage = "not a marc record".getBytes(Constants.DEFAULT_CHARSET);

        assertThrows(MarcException.class, () -> MarcUtils.iso2709ToRecord(garbage));
    }

    @Test
    void wellFormedRecordDoesNotNeedLenientParse() {
        assertFalse(
                Iso2709.needsLenientParse(
                        Iso2709.write(recordWith(field("245", '1', '0', 'a', "Título")))));
    }

    @Test
    void recordDtoLoadsCappedRowAndWritesItSplit() {
        String contents = words("Capítulo ", 2500);

        BiblioRecordDTO dto = new BiblioRecordDTO();

        dto.setIso2709(corruptedRecord(contents));
        dto.setId(42);

        byte[] healed = dto.getIso2709();

        assertFalse(Iso2709.needsLenientParse(healed));
        assertFieldLengthsFit(healed);
        assertEquals(contents, concatenate(dataFields(readStrict(healed), "505"), 'a'));
    }

    @Test
    void headBreaksAfterWhitespaceOnCodePointBoundary() {
        assertEquals("ação ", Iso2709.head("ação ação", 8));
        assertEquals("aç", Iso2709.head("açã", 4));
        assertEquals("a", Iso2709.head("a😀", 4));
    }

    private static byte[] corruptedRecord(String contents) {
        return writeWithMarc4j(
                recordWith(
                        field("245", '1', '0', 'a', "Título"),
                        field("505", '0', ' ', 'a', contents),
                        field("650", ' ', '4', 'a', "Assunto")));
    }

    private static Record recordWith(DataField... fields) {
        Record record =
                FACTORY.newRecord(MarcUtils.createBasicLeader(MaterialType.BOOK, RecordStatus.NEW));

        record.addVariableField(FACTORY.newControlField("001", "0000042"));

        for (DataField field : fields) {
            record.addVariableField(field);
        }

        return record;
    }

    private static DataField field(String tag, char ind1, char ind2, char code, String data) {
        DataField field = FACTORY.newDataField(tag, ind1, ind2);

        field.addSubfield(FACTORY.newSubfield(code, data));

        return field;
    }

    private static String words(String word, int count) {
        return word.repeat(count);
    }

    private static byte[] writeWithMarc4j(Record record) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        MarcStreamWriter writer = new MarcStreamWriter(os, Constants.DEFAULT_CHARSET_NAME);

        writer.write(record);
        writer.close();

        return os.toByteArray();
    }

    private static Record readStrict(byte[] iso2709) {
        return new MarcStreamReader(new ByteArrayInputStream(iso2709), "UTF-8").next();
    }

    private static Record readPermissive(byte[] iso2709) {
        return new MarcPermissiveStreamReader(new ByteArrayInputStream(iso2709), true, true).next();
    }

    private static List<DataField> dataFields(Record record, String tag) {
        return record.getVariableFields(tag).stream()
                .map(DataField.class::cast)
                .collect(Collectors.toList());
    }

    private static String concatenate(List<DataField> fields, char code) {
        return fields.stream()
                .flatMap(field -> field.getSubfields(code).stream())
                .map(Subfield::getData)
                .collect(Collectors.joining());
    }

    private static void assertFieldLengthsFit(byte[] iso2709) {
        int baseAddress = Integer.parseInt(new String(iso2709, 12, 5, Constants.DEFAULT_CHARSET));

        for (int position = 24; position + 12 < baseAddress; position += 12) {
            String entry = new String(iso2709, position, 12, Constants.DEFAULT_CHARSET);

            int length = Integer.parseInt(entry.substring(3, 7));
            int start = baseAddress + Integer.parseInt(entry.substring(7, 12));

            assertTrue(length < Iso2709.MAX_FIELD_LENGTH + 1, entry);
            assertEquals(0x1E, iso2709[start + length - 1], entry);
        }
    }
}

package biblivre.marc;

import biblivre.core.utils.Constants;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcException;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

/**
 * ISO 2709 stores each field length in four digits. marc4j's {@link MarcStreamWriter} only rejects
 * lengths above 99999, so a field between 10000 and 99999 bytes is silently written with length
 * 9999 and the record can no longer be read back. Records are sanitized before writing, and rows
 * already stored that way are rebuilt from the directory offsets instead of the lengths.
 */
public final class Iso2709 {
    public static final int MAX_FIELD_LENGTH = 9999;

    private static final int LEADER_LENGTH = 24;

    private static final int DIRECTORY_ENTRY_LENGTH = 12;

    private static final byte FIELD_TERMINATOR = 0x1E;

    private static final byte RECORD_TERMINATOR = 0x1D;

    private static final byte SUBFIELD_DELIMITER = 0x1F;

    private static final String SEPARATORS = "\u001D\u001E\u001F";

    private static final String SEPARATOR_REPLACEMENTS = "   ";

    // Two indicators and the field terminator
    private static final int DATA_FIELD_OVERHEAD = 3;

    // Subfield delimiter and code
    private static final int SUBFIELD_OVERHEAD = 2;

    private Iso2709() {}

    public static byte[] write(Record record) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        MarcWriter writer = new MarcStreamWriter(os, Constants.DEFAULT_CHARSET_NAME);

        writer.write(sanitize(record));

        writer.close();

        return os.toByteArray();
    }

    /**
     * Returns a copy of the record that can be written as valid ISO 2709: separator characters in
     * field data become spaces, and data fields longer than {@value #MAX_FIELD_LENGTH} bytes are
     * split into several fields with the same tag and indicators. The leader is shared, so the
     * writer still updates the original record's length and base address.
     */
    public static Record sanitize(Record record) {
        MarcFactory factory = MarcFactory.newInstance();

        Record sanitized = factory.newRecord(record.getLeader());

        for (ControlField field : record.getControlFields()) {
            sanitized.addVariableField(
                    factory.newControlField(field.getTag(), clean(field.getData())));
        }

        for (DataField field : record.getDataFields()) {
            for (DataField part : split(factory, field)) {
                sanitized.addVariableField(part);
            }
        }

        return sanitized;
    }

    private static List<DataField> split(MarcFactory factory, DataField field) {
        List<DataField> parts = new ArrayList<>();

        DataField current = newPart(factory, field);

        int currentLength = DATA_FIELD_OVERHEAD;

        for (Subfield subfield : field.getSubfields()) {
            String data = clean(subfield.getData());

            if (currentLength + SUBFIELD_OVERHEAD + utf8Length(data) > MAX_FIELD_LENGTH
                    && !current.getSubfields().isEmpty()) {
                parts.add(current);
                current = newPart(factory, field);
                currentLength = DATA_FIELD_OVERHEAD;
            }

            // Only an empty part gets here, so the subfield alone is too long
            while (currentLength + SUBFIELD_OVERHEAD + utf8Length(data) > MAX_FIELD_LENGTH) {
                String head =
                        head(data, MAX_FIELD_LENGTH - DATA_FIELD_OVERHEAD - SUBFIELD_OVERHEAD);

                current.addSubfield(factory.newSubfield(subfield.getCode(), head));
                parts.add(current);

                current = newPart(factory, field);
                data = data.substring(head.length());
            }

            current.addSubfield(factory.newSubfield(subfield.getCode(), data));
            currentLength += SUBFIELD_OVERHEAD + utf8Length(data);
        }

        parts.add(current);

        return parts;
    }

    private static DataField newPart(MarcFactory factory, DataField field) {
        return factory.newDataField(field.getTag(), field.getIndicator1(), field.getIndicator2());
    }

    /**
     * Longest prefix of {@code data} that fits in {@code maxBytes} of UTF-8, preferring to break
     * after whitespace in its second half so words are not cut.
     */
    static String head(String data, int maxBytes) {
        int bytes = 0;
        int end = 0;
        int lastBreak = -1;

        while (end < data.length()) {
            int codePoint = data.codePointAt(end);
            int codePointBytes = utf8Length(codePoint);

            if (bytes + codePointBytes > maxBytes) {
                break;
            }

            bytes += codePointBytes;
            end += Character.charCount(codePoint);

            if (Character.isWhitespace(codePoint)) {
                lastBreak = end;
            }
        }

        return data.substring(0, lastBreak > end / 2 ? lastBreak : end);
    }

    static int utf8Length(String data) {
        if (data == null) {
            return 0;
        }

        return data.getBytes(StandardCharsets.UTF_8).length;
    }

    private static int utf8Length(int codePoint) {
        if (codePoint < 0x80) {
            return 1;
        }

        if (codePoint < 0x800) {
            return 2;
        }

        return codePoint < 0x10000 ? 3 : 4;
    }

    private static String clean(String data) {
        return StringUtils.replaceChars(data, SEPARATORS, SEPARATOR_REPLACEMENTS);
    }

    /**
     * Whether marc4j can't be trusted to read these bytes: a directory length that does not end
     * where the next field starts (the length was capped at 9999), or a field or record terminator
     * inside field data.
     */
    public static boolean needsLenientParse(byte[] iso2709) {
        Directory directory = Directory.read(iso2709);

        for (Entry entry : directory.entries()) {
            int end = directory.endOf(entry);

            if (entry.start() + entry.length() != end) {
                return true;
            }

            for (int i = entry.start(); i < end - 1; i++) {
                if (iso2709[i] == FIELD_TERMINATOR || iso2709[i] == RECORD_TERMINATOR) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Reads a record using only the directory offsets: each field runs up to where the next one
     * starts. Field and record terminators inside a field become spaces. The result goes back
     * through {@link #sanitize} when it is written, which splits fields that are too long.
     *
     * @throws MarcException if the leader or directory can't be read
     */
    public static Record parseLenient(byte[] iso2709) {
        Directory directory = Directory.read(iso2709);

        MarcFactory factory = MarcFactory.newInstance();

        Record record = factory.newRecord(factory.newLeader(directory.leader()));

        for (Entry entry : directory.entries()) {
            byte[] field = Arrays.copyOfRange(iso2709, entry.start(), directory.endOf(entry));

            int length = field.length;

            if (length > 0 && field[length - 1] == FIELD_TERMINATOR) {
                length--;
            }

            for (int i = 0; i < length; i++) {
                if (field[i] == FIELD_TERMINATOR || field[i] == RECORD_TERMINATOR) {
                    field[i] = ' ';
                }
            }

            if (entry.tag().startsWith("00")) {
                record.addVariableField(
                        factory.newControlField(entry.tag(), decode(field, 0, length)));
            } else {
                record.addVariableField(parseDataField(factory, entry.tag(), field, length));
            }
        }

        return record;
    }

    private static DataField parseDataField(
            MarcFactory factory, String tag, byte[] field, int length) {
        char ind1 = length > 0 ? (char) (field[0] & 0xFF) : ' ';
        char ind2 = length > 1 ? (char) (field[1] & 0xFF) : ' ';

        DataField dataField = factory.newDataField(tag, ind1, ind2);

        int start = -1;

        for (int i = 2; i <= length; i++) {
            if (i < length && field[i] != SUBFIELD_DELIMITER) {
                continue;
            }

            // A subfield needs at least its code after the delimiter
            if (start >= 0 && i > start + 1) {
                String subfield = decode(field, start + 1, i - start - 1);

                dataField.addSubfield(
                        factory.newSubfield(subfield.charAt(0), subfield.substring(1)));
            }

            start = i;
        }

        return dataField;
    }

    private static String decode(byte[] bytes, int offset, int length) {
        return new String(bytes, offset, length, Constants.DEFAULT_CHARSET);
    }

    private record Entry(String tag, int length, int start) {}

    private record Directory(String leader, List<Entry> entries, int[] starts, int dataEnd) {
        static Directory read(byte[] iso2709) {
            if (iso2709 == null || iso2709.length < LEADER_LENGTH) {
                throw new MarcException("Record is shorter than its leader");
            }

            int dataEnd = iso2709.length;

            if (iso2709[dataEnd - 1] == RECORD_TERMINATOR) {
                dataEnd--;
            }

            String leader = new String(iso2709, 0, LEADER_LENGTH, StandardCharsets.US_ASCII);

            int baseAddress = parseNumber(leader.substring(12, 17));

            if (baseAddress <= LEADER_LENGTH
                    || baseAddress > dataEnd
                    || iso2709[baseAddress - 1] != FIELD_TERMINATOR) {
                throw new MarcException("Invalid base address of data: " + baseAddress);
            }

            List<Entry> entries = new ArrayList<>();

            for (int position = LEADER_LENGTH;
                    position + DIRECTORY_ENTRY_LENGTH < baseAddress;
                    position += DIRECTORY_ENTRY_LENGTH) {
                String entry =
                        new String(
                                iso2709,
                                position,
                                DIRECTORY_ENTRY_LENGTH,
                                StandardCharsets.US_ASCII);

                int start = baseAddress + parseNumber(entry.substring(7, 12));

                if (start > dataEnd) {
                    throw new MarcException("Field " + entry + " starts past the end of record");
                }

                entries.add(
                        new Entry(
                                entry.substring(0, 3), parseNumber(entry.substring(3, 7)), start));
            }

            int[] starts = entries.stream().mapToInt(Entry::start).sorted().distinct().toArray();

            return new Directory(leader, entries, starts, dataEnd);
        }

        int endOf(Entry entry) {
            int index = Arrays.binarySearch(starts, entry.start());

            return index + 1 < starts.length ? starts[index + 1] : dataEnd;
        }

        private static int parseNumber(String digits) {
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException e) {
                throw new MarcException("Invalid number in leader or directory: " + digits, e);
            }
        }
    }
}

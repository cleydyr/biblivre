package biblivre.cataloging.isbnlookup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

final class MarcAssertions {

    private MarcAssertions() {}

    /** Values of every {@code tag $code} occurrence, in record order. */
    static List<String> values(Record record, String tag, char code) {
        return record.getDataFields().stream()
                .filter(field -> field.getTag().equals(tag))
                .flatMap(field -> field.getSubfields(code).stream())
                .map(Subfield::getData)
                .toList();
    }

    static DataField single(Record record, String tag) {
        List<DataField> fields =
                record.getDataFields().stream().filter(f -> f.getTag().equals(tag)).toList();

        assertEquals(1, fields.size(), "occurrences of " + tag);

        return fields.get(0);
    }

    static List<String> tags(Record record) {
        return record.getDataFields().stream().map(DataField::getTag).toList();
    }
}

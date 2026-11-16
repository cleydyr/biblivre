package biblivre.cataloging.isbnlookup;

import biblivre.marc.MarcUtils;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;

/**
 * Converts {@link BookMetadata} into a new MARC21 bibliographic record for a book.
 *
 * <p>Author names are kept as the provider writes them; the cataloger reviews entry forms before
 * saving.
 */
public final class BookMetadataMarcMapper {

    /** Upper bound on 650 fields, since some providers return dozens of loose tags. */
    static final int MAX_SUBJECTS = 10;

    private static final Pattern YEAR = Pattern.compile("(\\d{4})");

    /** ISO 639-2 codes where the bibliographic (B) form, used by MARC, differs from Java's (T). */
    private static final Map<String, String> TERMINOLOGY_TO_BIBLIOGRAPHIC =
            Map.ofEntries(
                    Map.entry("bod", "tib"),
                    Map.entry("ces", "cze"),
                    Map.entry("cym", "wel"),
                    Map.entry("deu", "ger"),
                    Map.entry("ell", "gre"),
                    Map.entry("eus", "baq"),
                    Map.entry("fas", "per"),
                    Map.entry("fra", "fre"),
                    Map.entry("hye", "arm"),
                    Map.entry("isl", "ice"),
                    Map.entry("kat", "geo"),
                    Map.entry("mkd", "mac"),
                    Map.entry("mri", "mao"),
                    Map.entry("msa", "may"),
                    Map.entry("mya", "bur"),
                    Map.entry("nld", "dut"),
                    Map.entry("ron", "rum"),
                    Map.entry("slk", "slo"),
                    Map.entry("sqi", "alb"),
                    Map.entry("zho", "chi"));

    private BookMetadataMarcMapper() {}

    public static Record toRecord(BookMetadata book) {
        MarcFactory factory = MarcFactory.newInstance();
        Record record =
                factory.newRecord(MarcUtils.createBasicLeader(MaterialType.BOOK, RecordStatus.NEW));

        List<String> authors = nonBlank(book.authors());
        boolean hasMainEntry = !authors.isEmpty();

        add(record, field(factory, "020", ' ', ' ', 'a', book.isbn()));
        // Blank first indicator: providers do not say whether the book is a translation.
        add(record, field(factory, "041", ' ', ' ', 'a', book.language()));

        if (hasMainEntry) {
            add(record, field(factory, "100", '1', ' ', 'a', authors.get(0)));
        }

        add(
                record,
                field(
                        factory,
                        "245",
                        hasMainEntry ? '1' : '0',
                        '0',
                        'a',
                        book.title(),
                        'b',
                        book.subtitle(),
                        'c',
                        book.statementOfResponsibility()));
        add(record, field(factory, "250", ' ', ' ', 'a', book.edition()));
        add(
                record,
                field(
                        factory,
                        "260",
                        ' ',
                        ' ',
                        'a',
                        book.publicationPlace(),
                        'b',
                        book.publisher(),
                        'c',
                        book.publicationYear()));

        if (book.pageCount() != null && book.pageCount() > 0) {
            add(record, field(factory, "300", ' ', ' ', 'a', book.pageCount() + " p."));
        }

        add(record, field(factory, "520", ' ', ' ', 'a', book.summary()));

        nonBlank(book.subjects()).stream()
                .distinct()
                .limit(MAX_SUBJECTS)
                .forEach(subject -> add(record, field(factory, "650", ' ', '4', 'a', subject)));

        authors.stream()
                .skip(1)
                .forEach(author -> add(record, field(factory, "700", '1', ' ', 'a', author)));

        return record;
    }

    /** First four-digit year in a free-form date ("2009-05-12", "October 1, 1988"). */
    static String extractYear(String date) {
        if (date == null) {
            return "";
        }

        Matcher matcher = YEAR.matcher(date);

        return matcher.find() ? matcher.group(1) : "";
    }

    /** ISO 639-1 or 639-2 code to the MARC (ISO 639-2/B) language code, or blank. */
    static String toMarcLanguage(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return "";
        }

        String code = languageCode.trim().toLowerCase(Locale.ROOT);

        int separator = code.indexOf('-');
        if (separator > 0) {
            code = code.substring(0, separator);
        }

        if (code.length() == 3) {
            return TERMINOLOGY_TO_BIBLIOGRAPHIC.getOrDefault(code, code);
        }

        try {
            String terminology = Locale.of(code).getISO3Language();

            return TERMINOLOGY_TO_BIBLIOGRAPHIC.getOrDefault(terminology, terminology);
        } catch (MissingResourceException e) {
            return "";
        }
    }

    private static void add(Record record, DataField field) {
        if (field != null) {
            record.addVariableField(field);
        }
    }

    /** Builds a data field from (code, value) pairs, skipping blank values; null if all blank. */
    private static DataField field(
            MarcFactory factory, String tag, char ind1, char ind2, Object... codesAndValues) {
        DataField field = factory.newDataField(tag, ind1, ind2);

        for (int i = 0; i < codesAndValues.length; i += 2) {
            char code = (Character) codesAndValues[i];
            String value = (String) codesAndValues[i + 1];

            if (value != null && !value.isBlank()) {
                field.addSubfield(factory.newSubfield(code, value.trim()));
            }
        }

        return field.getSubfields().isEmpty() ? null : field;
    }

    private static List<String> nonBlank(List<String> values) {
        List<String> result = new ArrayList<>();

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                result.add(value.trim());
            }
        }

        return result;
    }
}

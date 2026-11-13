package biblivre.cataloging.isbnlookup;

import static biblivre.cataloging.isbnlookup.MarcAssertions.single;
import static biblivre.cataloging.isbnlookup.MarcAssertions.tags;
import static biblivre.cataloging.isbnlookup.MarcAssertions.values;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.marc.MaterialType;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

class BookMetadataMarcMapperTest {

    private static BookMetadata book(
            List<String> authors, String title, Integer pageCount, List<String> subjects) {
        return new BookMetadata(
                "9788535914849",
                title,
                "romance",
                "George Orwell",
                authors,
                "1. ed.",
                "São Paulo",
                "Companhia das Letras",
                "2009",
                pageCount,
                "por",
                "Distopia clássica.",
                subjects);
    }

    @Test
    void toRecord_mapsEveryFieldToMarc21() {
        Record record =
                BookMetadataMarcMapper.toRecord(
                        book(
                                List.of("George Orwell", "Heloisa Jahn"),
                                "1984",
                                416,
                                List.of("Ficção", "Distopia")));

        assertEquals(
                List.of(
                        "020", "041", "100", "245", "250", "260", "300", "520", "650", "650",
                        "700"),
                tags(record));
        assertEquals(List.of("9788535914849"), values(record, "020", 'a'));
        assertEquals(List.of("por"), values(record, "041", 'a'));
        assertEquals(List.of("George Orwell"), values(record, "100", 'a'));
        assertEquals(List.of("1984"), values(record, "245", 'a'));
        assertEquals(List.of("romance"), values(record, "245", 'b'));
        assertEquals(List.of("George Orwell"), values(record, "245", 'c'));
        assertEquals(List.of("1. ed."), values(record, "250", 'a'));
        assertEquals(List.of("São Paulo"), values(record, "260", 'a'));
        assertEquals(List.of("Companhia das Letras"), values(record, "260", 'b'));
        assertEquals(List.of("2009"), values(record, "260", 'c'));
        assertEquals(List.of("416 p."), values(record, "300", 'a'));
        assertEquals(List.of("Distopia clássica."), values(record, "520", 'a'));
        assertEquals(List.of("Ficção", "Distopia"), values(record, "650", 'a'));
        assertEquals(List.of("Heloisa Jahn"), values(record, "700", 'a'));

        assertEquals(' ', single(record, "041").getIndicator1(), "translation status is unknown");

        DataField title = single(record, "245");
        assertEquals('1', title.getIndicator1());
        assertEquals('0', title.getIndicator2());
        assertTrue(
                record.getDataFields().stream()
                        .filter(field -> field.getTag().equals("650"))
                        .allMatch(field -> field.getIndicator2() == '4'));

        assertEquals('n', record.getLeader().getRecordStatus());
        assertEquals(MaterialType.BOOK, MaterialType.fromRecord(record));
    }

    @Test
    void toRecord_skipsBlankValuesAndEmptyFields() {
        BookMetadata sparse =
                new BookMetadata(
                        "9788535914849",
                        "1984",
                        " ",
                        null,
                        List.of(),
                        "",
                        "",
                        "",
                        "",
                        null,
                        "",
                        null,
                        null);

        Record record = BookMetadataMarcMapper.toRecord(sparse);

        assertEquals(List.of("020", "245"), tags(record));

        DataField title = single(record, "245");
        assertEquals(1, title.getSubfields().size());
        assertEquals('0', title.getIndicator1(), "no 100 main entry, so 245 is the main entry");
    }

    @Test
    void toRecord_limitsAndDeduplicatesSubjects() {
        List<String> subjects =
                IntStream.range(0, 25).mapToObj(i -> "Assunto " + (i % 15)).toList();

        Record record = BookMetadataMarcMapper.toRecord(book(List.of(), "1984", null, subjects));

        List<String> mapped = values(record, "650", 'a');
        assertEquals(BookMetadataMarcMapper.MAX_SUBJECTS, mapped.size());
        assertEquals(mapped.stream().distinct().count(), mapped.size());
        assertFalse(tags(record).contains("100"));
        assertFalse(tags(record).contains("300"));
    }

    @Test
    void extractYear_findsFirstFourDigitYear() {
        assertEquals("2009", BookMetadataMarcMapper.extractYear("2009-05-12"));
        assertEquals("1988", BookMetadataMarcMapper.extractYear("October 1, 1988"));
        assertEquals("", BookMetadataMarcMapper.extractYear("s.d."));
        assertEquals("", BookMetadataMarcMapper.extractYear(null));
    }

    @Test
    void toMarcLanguage_returnsBibliographicCodes() {
        assertEquals("por", BookMetadataMarcMapper.toMarcLanguage("pt"));
        assertEquals("por", BookMetadataMarcMapper.toMarcLanguage("pt-BR"));
        assertEquals("eng", BookMetadataMarcMapper.toMarcLanguage("en"));
        assertEquals("fre", BookMetadataMarcMapper.toMarcLanguage("fr"));
        assertEquals("ger", BookMetadataMarcMapper.toMarcLanguage("de"));
        assertEquals("chi", BookMetadataMarcMapper.toMarcLanguage("zho"));
        assertEquals("", BookMetadataMarcMapper.toMarcLanguage(""));
        assertEquals("", BookMetadataMarcMapper.toMarcLanguage(null));
    }
}

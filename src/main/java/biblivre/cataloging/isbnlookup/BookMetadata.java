package biblivre.cataloging.isbnlookup;

import java.util.List;

/**
 * Provider-neutral book description, converted to MARC21 by {@link BookMetadataMarcMapper}.
 *
 * <p>Every field may be blank or empty when the provider does not supply it.
 *
 * @param isbn the ISBN to record in 020 $a
 * @param language ISO 639-2/B code (e.g. {@code por})
 * @param pageCount number of pages, or {@code null}
 */
public record BookMetadata(
        String isbn,
        String title,
        String subtitle,
        String statementOfResponsibility,
        List<String> authors,
        String edition,
        String publicationPlace,
        String publisher,
        String publicationYear,
        Integer pageCount,
        String language,
        String summary,
        List<String> subjects) {

    public BookMetadata {
        authors = authors == null ? List.of() : List.copyOf(authors);
        subjects = subjects == null ? List.of() : List.copyOf(subjects);
    }
}

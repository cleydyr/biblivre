package biblivre.cataloging.isbnlookup;

import java.util.Optional;
import org.marc4j.marc.Record;

/**
 * A source of bibliographic metadata keyed by ISBN.
 *
 * <p>To add a provider, register a Spring bean implementing this interface; it becomes selectable
 * through {@code biblivre.cataloging.isbn-lookup.provider} by its {@link #name()}.
 */
public interface IsbnMetadataProvider {

    /** Name used in {@code biblivre.cataloging.isbn-lookup.provider}. */
    String name();

    /**
     * Looks up a book by ISBN.
     *
     * @param isbn a normalized, checksum-valid ISBN-10 or ISBN-13 (digits, plus a trailing {@code
     *     X} for some ISBN-10s)
     * @return the book as a MARC21 bibliographic record, or empty when the provider does not know
     *     the ISBN
     * @throws RuntimeException when the provider cannot be reached or answers with an error
     */
    Optional<Record> lookup(String isbn);
}

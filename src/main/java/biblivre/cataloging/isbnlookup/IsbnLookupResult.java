package biblivre.cataloging.isbnlookup;

import org.marc4j.marc.Record;

/**
 * @param isbn the normalized ISBN that was looked up
 * @param provider name of the provider that answered
 * @param record the book as a new MARC21 bibliographic record
 */
public record IsbnLookupResult(String isbn, String provider, Record record) {}

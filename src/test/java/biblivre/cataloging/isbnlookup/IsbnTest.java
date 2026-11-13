package biblivre.cataloging.isbnlookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IsbnTest {

    @Test
    void normalize_keepsDigitsAndUpperCaseX() {
        assertEquals("9788535914849", Isbn.normalize("978-85-359-1484-9"));
        assertEquals("080442957X", Isbn.normalize(" 0-8044-2957-x "));
        assertEquals("", Isbn.normalize(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"9788535914849", "9780140328721", "0140328726", "080442957X"})
    void isValid_acceptsCorrectCheckDigits(String isbn) {
        assertTrue(Isbn.isValid(isbn));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "9788535914848", // wrong ISBN-13 check digit
                "0140328727", // wrong ISBN-10 check digit
                "1234567890123", // 13 digits without the 978/979 prefix
                "97885359148", // too short
                "X140328726", // X only allowed as the ISBN-10 check digit
                ""
            })
    void isValid_rejectsInvalidValues(String isbn) {
        assertFalse(Isbn.isValid(isbn));
    }

    @Test
    void toIsbn13_convertsIsbn10() {
        assertEquals("9780140328721", Isbn.toIsbn13("0140328726"));
        assertEquals("9788535914849", Isbn.toIsbn13("9788535914849"));
    }
}

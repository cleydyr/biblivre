package biblivre.circulation.addresslookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.circulation.addresslookup.AddressLookupException.Reason;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class AddressLookupServiceTest {

    @Test
    void normalizeCep_stripsNonDigits() {
        assertEquals("01310100", AddressLookupService.normalizeCep("01310-100"));
        assertEquals("01310100", AddressLookupService.normalizeCep(" 01310 100 "));
        assertEquals("", AddressLookupService.normalizeCep(null));
    }

    @Test
    void isValidBrazilianCep_requiresEightDigits() {
        assertTrue(AddressLookupService.isValidBrazilianCep("01310100"));
        assertFalse(AddressLookupService.isValidBrazilianCep("1310100"));
        assertFalse(AddressLookupService.isValidBrazilianCep("0131010a"));
        assertFalse(AddressLookupService.isValidBrazilianCep(""));
    }

    @Test
    void lookup_whenDisabled_throwsDisabled() {
        AddressLookupProperties properties = new AddressLookupProperties();
        properties.setEnabled(false);

        AddressLookupService service = new AddressLookupService(properties, RestClient.builder());

        AddressLookupException exception =
                assertThrows(AddressLookupException.class, () -> service.lookup("01310100"));
        assertEquals(Reason.DISABLED, exception.getReason());
    }

    @Test
    void lookup_whenInvalidCep_throwsInvalid() {
        AddressLookupProperties properties = new AddressLookupProperties();
        properties.setEnabled(true);

        AddressLookupService service = new AddressLookupService(properties, RestClient.builder());

        AddressLookupException exception =
                assertThrows(AddressLookupException.class, () -> service.lookup("123"));
        assertEquals(Reason.INVALID_CEP, exception.getReason());
    }
}

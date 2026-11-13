package biblivre.cataloging.isbnlookup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.cataloging.isbnlookup.IsbnLookupException.Reason;
import biblivre.reports.generated.api.model.RestIsbnLookupResult;
import biblivre.reports.generated.api.model.RestMarcDatafield;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class CatalogingApiDelegateImplTest {

    @Test
    void statusFor_mapsEveryReason() {
        assertEquals(
                HttpStatus.BAD_REQUEST, CatalogingApiDelegateImpl.statusFor(Reason.INVALID_ISBN));
        assertEquals(HttpStatus.NOT_FOUND, CatalogingApiDelegateImpl.statusFor(Reason.NOT_FOUND));
        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                CatalogingApiDelegateImpl.statusFor(Reason.DISABLED));
        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                CatalogingApiDelegateImpl.statusFor(Reason.UPSTREAM_ERROR));
    }

    @Test
    void toRest_exposesDatafieldsAndHumanReadableMarc() {
        BookMetadata book =
                new BookMetadata(
                        "9788535914849",
                        "1984",
                        "",
                        "",
                        List.of("George Orwell"),
                        "",
                        "",
                        "Companhia das Letras",
                        "2009",
                        null,
                        "",
                        "",
                        List.of());

        RestIsbnLookupResult rest =
                CatalogingApiDelegateImpl.toRest(
                        new IsbnLookupResult(
                                "9788535914849",
                                "open-library",
                                BookMetadataMarcMapper.toRecord(book)));

        assertEquals("9788535914849", rest.getIsbn());
        assertEquals("open-library", rest.getProvider());
        assertEquals(
                List.of("020", "100", "245", "260"),
                rest.getDatafields().stream().map(RestMarcDatafield::getTag).toList());

        RestMarcDatafield title = rest.getDatafields().get(2);
        assertEquals("1", title.getInd1());
        assertEquals("0", title.getInd2());
        assertEquals("a", title.getSubfields().get(0).getCode());
        assertEquals("1984", title.getSubfields().get(0).getValue());

        RestMarcDatafield isbn = rest.getDatafields().get(0);
        assertEquals(" ", isbn.getInd1());

        assertTrue(rest.getMarc().contains("245 10"), rest.getMarc());
        assertTrue(rest.getMarc().contains("1984"), rest.getMarc());
    }
}

package biblivre.cataloging.isbnlookup;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.cataloging.isbnlookup.IsbnLookupException.Reason;
import biblivre.core.auth.LoggedUserAuthorization;
import biblivre.reports.generated.api.CatalogingApiDelegate;
import biblivre.reports.generated.api.model.RestIsbnLookupResult;
import biblivre.reports.generated.api.model.RestMarcDatafield;
import biblivre.reports.generated.api.model.RestMarcSubfield;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CatalogingApiDelegateImpl implements CatalogingApiDelegate {
    private final IsbnLookupService isbnLookupService;
    private final LoggedUserAuthorization loggedUserAuthorization;

    public CatalogingApiDelegateImpl(
            IsbnLookupService isbnLookupService, LoggedUserAuthorization loggedUserAuthorization) {
        this.isbnLookupService = isbnLookupService;
        this.loggedUserAuthorization = loggedUserAuthorization;
    }

    @Override
    public ResponseEntity<RestIsbnLookupResult> lookupBookByIsbn(String isbn) {
        if (!loggedUserAuthorization.isAllowed("cataloging.bibliographic", "save")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            return ResponseEntity.ok(toRest(isbnLookupService.lookup(isbn)));
        } catch (IsbnLookupException exception) {
            if (exception.getReason() == Reason.UPSTREAM_ERROR) {
                log.warn("ISBN lookup failed", exception);
            }

            return ResponseEntity.status(statusFor(exception.getReason())).build();
        }
    }

    static HttpStatus statusFor(Reason reason) {
        return switch (reason) {
            case INVALID_ISBN -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case DISABLED, UPSTREAM_ERROR -> HttpStatus.SERVICE_UNAVAILABLE;
        };
    }

    static RestIsbnLookupResult toRest(IsbnLookupResult result) {
        Record record = result.record();

        BiblioRecordDTO dto = new BiblioRecordDTO();
        dto.setRecord(record);

        RestIsbnLookupResult rest = new RestIsbnLookupResult();
        rest.setIsbn(result.isbn());
        rest.setProvider(result.provider());
        rest.setMarc(dto.getHumanReadableMarc());
        rest.setDatafields(
                record.getDataFields().stream()
                        .map(CatalogingApiDelegateImpl::toRestDatafield)
                        .toList());

        return rest;
    }

    private static RestMarcDatafield toRestDatafield(DataField field) {
        List<RestMarcSubfield> subfields =
                field.getSubfields().stream()
                        .map(CatalogingApiDelegateImpl::toRestSubfield)
                        .toList();

        RestMarcDatafield rest = new RestMarcDatafield();
        rest.setTag(field.getTag());
        rest.setInd1(String.valueOf(field.getIndicator1()));
        rest.setInd2(String.valueOf(field.getIndicator2()));
        rest.setSubfields(subfields);

        return rest;
    }

    private static RestMarcSubfield toRestSubfield(Subfield subfield) {
        RestMarcSubfield rest = new RestMarcSubfield();
        rest.setCode(String.valueOf(subfield.getCode()));
        rest.setValue(subfield.getData());

        return rest;
    }
}

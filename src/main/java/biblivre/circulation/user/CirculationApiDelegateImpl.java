package biblivre.circulation.user;

import biblivre.circulation.addresslookup.AddressLookupException;
import biblivre.circulation.addresslookup.AddressLookupException.Reason;
import biblivre.circulation.addresslookup.AddressLookupResult;
import biblivre.circulation.addresslookup.AddressLookupService;
import biblivre.circulation.addresslookup.CirculationAddressLookupAuth;
import biblivre.reports.generated.api.CirculationApiDelegate;
import biblivre.reports.generated.api.model.RestAddressLookupEnabled;
import biblivre.reports.generated.api.model.RestAddressLookupResult;
import biblivre.reports.generated.api.model.RestUserSearchableField;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CirculationApiDelegateImpl implements CirculationApiDelegate {
    private UserFieldBO userFieldBO;
    private AddressLookupService addressLookupService;
    private CirculationAddressLookupAuth addressLookupAuth;

    @Override
    public ResponseEntity<List<RestUserSearchableField>> getUserSearchableFields() {
        return new ResponseEntity<>(
                userFieldBO.getSearchableFields().stream()
                        .map(this::toRestUserSearchableField)
                        .toList(),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RestUserSearchableField>> getUserFields() {
        return new ResponseEntity<>(
                userFieldBO.getFields().stream().map(this::toRestUserSearchableField).toList(),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestAddressLookupEnabled> getAddressLookupEnabled() {
        if (!isAuthorizedForAddressLookup()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        RestAddressLookupEnabled body = new RestAddressLookupEnabled();
        body.setEnabled(addressLookupService.isEnabled());
        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<RestAddressLookupResult> lookupAddressByCep(String cep) {
        if (!isAuthorizedForAddressLookup()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            AddressLookupResult result = addressLookupService.lookup(cep);
            return ResponseEntity.ok(toRestAddressLookupResult(result));
        } catch (AddressLookupException exception) {
            return ResponseEntity.status(statusFor(exception.getReason())).build();
        }
    }

    private boolean isAuthorizedForAddressLookup() {
        return addressLookupAuth.canLookupAddress();
    }

    private static HttpStatus statusFor(Reason reason) {
        return switch (reason) {
            case DISABLED -> HttpStatus.SERVICE_UNAVAILABLE;
            case INVALID_CEP -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UPSTREAM_ERROR -> HttpStatus.SERVICE_UNAVAILABLE;
        };
    }

    private RestUserSearchableField toRestUserSearchableField(UserFieldDTO field) {
        RestUserSearchableField restField = new RestUserSearchableField();
        restField.setKey(field.getKey());
        restField.setType(RestUserSearchableField.TypeEnum.fromValue(field.getType().getString()));
        restField.setRequired(field.isRequired());
        restField.setMaxLength(field.getMaxLength());
        restField.setSortOrder(field.getSortOrder());
        return restField;
    }

    private static RestAddressLookupResult toRestAddressLookupResult(AddressLookupResult result) {
        RestAddressLookupResult restResult = new RestAddressLookupResult();
        restResult.setStreet(result.street());
        restResult.setNeighborhood(result.neighborhood());
        restResult.setCity(result.city());
        restResult.setState(result.state());
        restResult.setIncomplete(result.incomplete());
        return restResult;
    }

    @Autowired
    public void setUserFieldBO(UserFieldBO userFieldBO) {
        this.userFieldBO = userFieldBO;
    }

    @Autowired
    public void setAddressLookupService(AddressLookupService addressLookupService) {
        this.addressLookupService = addressLookupService;
    }

    @Autowired
    public void setAddressLookupAuth(CirculationAddressLookupAuth addressLookupAuth) {
        this.addressLookupAuth = addressLookupAuth;
    }
}

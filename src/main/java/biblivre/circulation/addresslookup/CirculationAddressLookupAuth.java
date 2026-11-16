package biblivre.circulation.addresslookup;

import biblivre.core.auth.LoggedUserAuthorization;
import org.springframework.stereotype.Component;

@Component
public class CirculationAddressLookupAuth {
    private final LoggedUserAuthorization loggedUserAuthorization;

    public CirculationAddressLookupAuth(LoggedUserAuthorization loggedUserAuthorization) {
        this.loggedUserAuthorization = loggedUserAuthorization;
    }

    public boolean canLookupAddress() {
        return loggedUserAuthorization.isAllowed("circulation.user", "save");
    }
}

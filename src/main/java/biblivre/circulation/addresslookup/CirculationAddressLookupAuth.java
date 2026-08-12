package biblivre.circulation.addresslookup;

import biblivre.core.SchemaThreadLocal;
import biblivre.core.auth.AuthorizationPoints;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class CirculationAddressLookupAuth {

    public boolean canLookupAddress(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String schema = SchemaThreadLocal.get();
        Object attribute = session.getAttribute(schema + ".logged_user_atps");
        if (!(attribute instanceof AuthorizationPoints authorizationPoints)) {
            return false;
        }

        return authorizationPoints.isAllowed("circulation.user", "save");
    }
}

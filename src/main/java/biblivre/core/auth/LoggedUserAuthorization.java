package biblivre.core.auth;

import biblivre.core.SchemaThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Checks the authorization points of the user logged in to the current request's schema, for REST
 * endpoints outside the legacy controller (which checks them itself).
 */
@Component
public class LoggedUserAuthorization {

    public boolean isAllowed(String module, String action) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return false;
        }

        HttpServletRequest request = requestAttributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String schema = SchemaThreadLocal.get();
        Object attribute = session.getAttribute(schema + ".logged_user_atps");
        if (!(attribute instanceof AuthorizationPoints authorizationPoints)) {
            return false;
        }

        return authorizationPoints.isAllowed(module, action);
    }
}

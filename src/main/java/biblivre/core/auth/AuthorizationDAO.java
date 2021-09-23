package biblivre.core.auth;

import biblivre.login.LoginDTO;
import java.util.Map;

public interface AuthorizationDAO {

    Map<String, Boolean> getUserPermissions(LoginDTO user);
}

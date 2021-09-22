package biblivre.core.auth;

import java.util.Map;

import biblivre.login.LoginDTO;

public interface AuthorizationDAO {

	Map<String, Boolean> getUserPermissions(LoginDTO user);

}
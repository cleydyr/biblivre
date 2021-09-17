package biblivre.administration.permissions;

import java.util.List;

import biblivre.circulation.user.UserDTO;

public interface PermissionDAO {

	boolean delete(UserDTO user);

	List<String> getByLoginId(Integer loginid);

	boolean save(int loginid, String permission);

	boolean save(int loginId, List<String> permissions);

}
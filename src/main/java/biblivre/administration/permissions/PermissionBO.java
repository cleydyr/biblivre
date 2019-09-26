package biblivre.administration.permissions;

import java.util.Collection;

import biblivre.circulation.user.UserDTO;

public interface PermissionBO {

	boolean deleteByUser(UserDTO user);

	boolean saveAll(Integer loginId, Collection<String> permissions);

	Collection<String> getPermissionsByLoginId(Integer loginid);

}
package biblivre.administration.permissions;

import java.util.Collection;

public interface PermissionDAO {

	boolean deleteByUserId(int loginId);

	Collection<String> getPermissionsByLoginId(Integer loginid);

	boolean save(int loginid, String permission);

	boolean saveAll(int loginId, Collection<String> permissions);

}
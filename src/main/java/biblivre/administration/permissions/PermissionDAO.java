package biblivre.administration.permissions;

import biblivre.circulation.user.UserDTO;
import java.util.Collection;
import java.util.List;

public interface PermissionDAO {

    boolean delete(UserDTO user);

    Collection<String> getByLoginId(Integer loginid);

    boolean save(int loginid, String permission);

    boolean save(int loginId, List<String> permissions);
}

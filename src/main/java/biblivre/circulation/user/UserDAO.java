package biblivre.circulation.user;

import biblivre.core.DTOCollection;
import biblivre.search.SearchException;
import java.util.Collection;
import java.util.Map;

public interface UserDAO {

    Map<Integer, UserDTO> map(Collection<Integer> ids);

    DTOCollection<UserDTO> search(UserSearchDTO dto, int limit, int offset) throws SearchException;

    UserDTO save(UserDTO user) throws SearchException;

    boolean delete(UserDTO user) throws SearchException;

    void markAsPrinted(Collection<Integer> ids) throws SearchException;

    boolean updateUserStatus(Integer userId, UserStatus status) throws SearchException;

    Integer getUserIdByLoginId(Integer loginId);

    Collection<UserDTO> listAllUsers();
}

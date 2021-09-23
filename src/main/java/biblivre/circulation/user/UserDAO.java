package biblivre.circulation.user;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserDAO {

    Map<Integer, UserDTO> map(Set<Integer> ids);

    DTOCollection<UserDTO> search(UserSearchDTO dto, int limit, int offset);

    boolean save(UserDTO user);

    boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

    boolean delete(UserDTO user);

    void markAsPrinted(Set<Integer> ids);

    boolean updateUserStatus(Integer userId, UserStatus status);

    Integer getUserIdByLoginId(Integer loginId);
}

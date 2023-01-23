package biblivre.administration.usertype;

import biblivre.core.DTOCollection;
import java.util.List;

public interface UserTypeDAO {

    UserTypeDTO get(int id);

    DTOCollection<UserTypeDTO> search(String value, int limit, int offset);

    List<UserTypeDTO> list();

    boolean save(UserTypeDTO dto);

    boolean delete(int id);
}

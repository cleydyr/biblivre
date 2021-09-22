package biblivre.administration.usertype;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import java.util.List;

public interface UserTypeDAO {

    UserTypeDTO get(int id);

    DTOCollection<UserTypeDTO> search(String value, int limit, int offset);

    List<UserTypeDTO> list();

    boolean save(UserTypeDTO dto);

    boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

    boolean delete(int id);
}

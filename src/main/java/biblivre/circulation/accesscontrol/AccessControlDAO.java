package biblivre.circulation.accesscontrol;

import biblivre.core.AbstractDTO;
import java.util.List;

public interface AccessControlDAO {

    boolean save(AccessControlDTO dto);

    boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

    boolean update(AccessControlDTO dto);

    AccessControlDTO getByCardId(Integer cardId);

    AccessControlDTO getByUserId(Integer userId);
}

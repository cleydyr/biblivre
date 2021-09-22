package biblivre.circulation.lending;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;
import java.util.List;

public interface LendingFineDAO {

    boolean insert(LendingFineDTO fine);

    boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

    LendingFineDTO get(Integer lendingFineId);

    LendingFineDTO getByLendingId(Integer lendingId);

    List<LendingFineDTO> list(UserDTO user, boolean pendingOnly);

    boolean update(LendingFineDTO fine);
}

package biblivre.circulation.lending;

import java.util.List;

import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;

public interface LendingFineDAO {

	boolean insert(LendingFineDTO fine);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	LendingFineDTO get(Integer lendingFineId);

	LendingFineDTO getByLendingId(Integer lendingId);

	List<LendingFineDTO> list(UserDTO user, boolean pendingOnly);

	boolean update(LendingFineDTO fine);

}
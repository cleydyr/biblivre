package biblivre.circulation.lending.persistence;

import biblivre.circulation.lending.LendingFineDTO;
import biblivre.circulation.user.UserDTO;
import java.util.List;

public interface LendingFineDAO {

    void insert(LendingFineDTO fine);

    LendingFineDTO get(Integer lendingFineId);

    LendingFineDTO getByLendingId(Integer lendingId);

    List<LendingFineDTO> list(UserDTO user, boolean pendingOnly);

    boolean update(LendingFineDTO fine);

    boolean adjustValue(Integer fineId, float value);

    boolean delete(Integer fineId);

    boolean hasPendingFine(int userID);
}

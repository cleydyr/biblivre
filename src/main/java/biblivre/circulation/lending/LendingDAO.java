package biblivre.circulation.lending;

import biblivre.cataloging.holding.HoldingDTO;
import biblivre.circulation.user.UserDTO;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LendingDAO {

    LendingDTO get(Integer id);

    LendingDTO getCurrentLending(HoldingDTO holding);

    Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> ids);

    List<LendingDTO> listHistory(HoldingDTO holding);

    List<LendingDTO> listLendings(UserDTO user);

    List<LendingDTO> listHistory(UserDTO user);

    Integer countLendings(UserDTO user);

    Integer countHistory(UserDTO user);

    List<LendingDTO> listByRecordId(int recordId);

    List<LendingDTO> listLendings(int offset, int limit);

    Integer countLendings();

    Integer getCurrentLendingsCount(UserDTO user);

    boolean doLend(LendingDTO lending);

    boolean doLend(LendingDTO lending, Connection con);

    void doReturn(int lendingId);

    boolean doReturn(int lendingId, Connection con);

    boolean doRenew(int lendingId, Date expectedReturnDate, int createdBy);

    Integer countLentHoldings(int recordId);

    LendingDTO getLatest(int holdingSerial, int userId);

    boolean hasLateLendings(int userId);
}

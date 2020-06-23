package biblivre.circulation.lending;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biblivre.core.AbstractDTO;

public interface ILendingDAO {

	LendingDTO get(int lendingId);

	LendingDTO getCurrentLending(Integer holdingId);

	Map<Integer, LendingDTO> getCurrentLendingMap(Set<Integer> lendingIds);

	List<LendingDTO> listHoldingHistory(int holdingId);

	List<LendingDTO> listUserLendings(int userId);

	List<LendingDTO> listUserHistory(int id);

	Integer userLendingsCount(int userId);

	Integer userHistoryCount(int userId);

	List<LendingDTO> listByRecordId(int recordId);

	List<LendingDTO> listLendings(int offset, int limit);

	int countLendings();

	int getCurrentLendingsCount(int userId);

	boolean doLend(LendingDTO lending);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	boolean doReturn(int lendingId);

	boolean doRenew(int lendingId, Date expectedReturnDate, int createdBy);

	Integer countLentHoldings(int recordId);

	LendingDTO getLatest(int holdingSerial, int userId);

}
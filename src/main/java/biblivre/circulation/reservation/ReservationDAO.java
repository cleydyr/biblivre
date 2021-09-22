package biblivre.circulation.reservation;

import java.util.List;
import java.util.Map;
import java.util.Set;

import biblivre.cataloging.RecordDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;

public interface ReservationDAO {

	ReservationDTO get(Integer id);

	List<ReservationDTO> list();

	List<ReservationDTO> list(UserDTO user, RecordDTO record);

	int count();

	int count(UserDTO user, RecordDTO record);

	boolean deleteExpired();

	boolean delete(Integer id);

	boolean delete(Integer userId, Integer recordId);

	int insert(ReservationDTO dto);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	Map<Integer, List<ReservationDTO>> getReservationsMap(Set<Integer> recordIds);

}
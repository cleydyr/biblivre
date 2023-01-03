package biblivre.circulation.reservation;

import biblivre.cataloging.RecordDTO;
import biblivre.circulation.user.UserDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReservationDAO {

    ReservationDTO get(Integer id);

    List<ReservationDTO> list(int defaultSortableGroupId);

    List<ReservationDTO> list(UserDTO user, RecordDTO record, int defaultSortableGroupId);

    int count();

    int count(UserDTO user, RecordDTO record);

    boolean deleteExpired();

    boolean delete(Integer id);

    boolean delete(Integer userId, Integer recordId);

    int insert(ReservationDTO dto);

    Map<Integer, List<ReservationDTO>> getReservationsMap(Set<Integer> recordIds);
}

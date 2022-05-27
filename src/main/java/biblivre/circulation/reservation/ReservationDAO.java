package biblivre.circulation.reservation;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    int countByRecord(BiblioRecordDTO biblioRecordDTO);
}

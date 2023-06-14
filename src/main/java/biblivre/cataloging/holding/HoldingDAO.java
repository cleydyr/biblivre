package biblivre.cataloging.holding;

import biblivre.cataloging.RecordDAO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.search.SearchDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.DTOCollection;
import biblivre.login.LoginDTO;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface HoldingDAO extends RecordDAO {

    Integer count(int recordId, boolean availableOnly);

    HoldingDTO getByAccessionNumber(String accessionNumber);

    Map<Integer, RecordDTO> map(Set<Integer> ids);

    List<RecordDTO> list(int offset, int limit);

    boolean save(RecordDTO dto);

    void updateHoldingCreationCounter(UserDTO dto, LoginDTO ldto);

    boolean update(RecordDTO dto);

    void markAsPrinted(Set<Integer> ids);

    boolean delete(RecordDTO dto);

    int getNextAccessionNumber(String accessionPrefix);

    boolean isAccessionNumberAvailable(String accessionNumber, int holdingId);

    DTOCollection<HoldingDTO> list(int recordId);

    DTOCollection<HoldingDTO> search(
            String query, RecordDatabase database, boolean lentOnly, int offset, int limit);

    Integer count(SearchDTO search);

    Map<Integer, Integer> countSearchResults(SearchDTO search);

    List<RecordDTO> getSearchResults(SearchDTO search);
}

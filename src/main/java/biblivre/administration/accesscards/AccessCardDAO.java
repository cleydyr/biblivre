package biblivre.administration.accesscards;

import biblivre.core.DTOCollection;
import java.util.ArrayList;
import java.util.List;

public interface AccessCardDAO {

    AccessCardDTO get(String code);

    List<AccessCardDTO> get(List<String> codes, List<AccessCardStatus> status);

    AccessCardDTO get(int id);

    DTOCollection<AccessCardDTO> search(
            String code, AccessCardStatus status, int limit, int offset);

    boolean save(AccessCardDTO dto);

    boolean save(ArrayList<AccessCardDTO> cardList);

    boolean update(AccessCardDTO dto);

    boolean delete(int id);
}

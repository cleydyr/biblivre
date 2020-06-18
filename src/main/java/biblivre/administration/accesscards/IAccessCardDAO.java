package biblivre.administration.accesscards;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public interface IAccessCardDAO {

	Collection<AccessCardDTO> get(List<String> codes);

	AccessCardDTO get(int id);

	DTOCollection<AccessCardDTO> search(String code, AccessCardStatus status, int limit, int offset);

	AccessCardDTO create();

	boolean save(LinkedList<AccessCardDTO> cardList);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	boolean update(AccessCardDTO dto);

	boolean delete(int id);

}
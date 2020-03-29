package biblivre.administration.accesscards;

import java.util.LinkedList;
import java.util.List;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;

public interface AccessCardPersistence {

	AccessCardDTO get(String code);

	List<AccessCardDTO> get(List<String> codes, List<AccessCardStatus> status);

	AccessCardDTO get(int id);

	DTOCollection<AccessCardDTO> search(String code, AccessCardStatus status, int limit, int offset);

	boolean save(AccessCardDTO dto);

	boolean save(LinkedList<AccessCardDTO> cardList);

	boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

	boolean update(AccessCardDTO dto);

	boolean delete(int id);

	public void setSchema(String schema);
}
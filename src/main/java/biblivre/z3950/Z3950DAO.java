package biblivre.z3950;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import java.util.List;

public interface Z3950DAO {

    List<Z3950AddressDTO> listAll();

    DTOCollection<Z3950AddressDTO> search(String value, int limit, int offset);

    List<Z3950AddressDTO> list(List<Integer> ids);

    boolean insert(Z3950AddressDTO dto);

    boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

    boolean update(Z3950AddressDTO dto);

    boolean delete(Z3950AddressDTO dto);
}

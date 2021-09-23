package biblivre.acquisition.supplier;

import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import java.util.List;

public interface SupplierDAO {

    boolean save(SupplierDTO dto);

    boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList);

    boolean update(SupplierDTO dto);

    boolean delete(SupplierDTO dto);

    SupplierDTO get(int id);

    DTOCollection<SupplierDTO> search(String value, int limit, int offset);
}

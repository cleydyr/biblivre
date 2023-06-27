package biblivre.acquisition.supplier;

import biblivre.core.DTOCollection;

public interface SupplierDAO {

    boolean save(SupplierDTO dto);

    boolean update(SupplierDTO dto);

    boolean delete(int id);

    SupplierDTO get(int id);

    DTOCollection<SupplierDTO> search(String value, int limit, int offset);
}

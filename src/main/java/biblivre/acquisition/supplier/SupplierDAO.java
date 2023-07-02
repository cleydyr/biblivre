package biblivre.acquisition.supplier;

import biblivre.core.DTOCollection;

public interface SupplierDAO {

    int save(SupplierDTO dto);

    void update(SupplierDTO dto);

    boolean delete(int id);

    SupplierDTO get(int id);

    DTOCollection<SupplierDTO> search(String value, int limit, int offset);
}

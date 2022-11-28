package biblivre.core.schemas;

import java.util.Set;

public interface SchemaDAO {

    Set<SchemaDTO> list();

    boolean insert(SchemaDTO dto);

    boolean delete(SchemaDTO dto);

    boolean save(SchemaDTO dto);

    boolean exists(String schema);
}

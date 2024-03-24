package biblivre.core.schemas;

import java.util.Set;
import org.springframework.core.io.Resource;

public interface SchemaDAO {
    Set<SchemaDTO> list();

    boolean delete(SchemaDTO dto);

    boolean save(SchemaDTO dto);

    boolean exists(String schema);

    boolean createSchema(SchemaDTO dto, boolean addToGlobal, Resource databaseTemplate);
}

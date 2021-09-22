package biblivre.administration.backup;

import java.util.List;
import java.util.Set;

public interface BackupDAO {

    boolean save(BackupDTO dto);

    BackupDTO get(Integer id);

    Set<String> listDatabaseSchemas();

    List<BackupDTO> list();

    List<BackupDTO> list(int limit);
}

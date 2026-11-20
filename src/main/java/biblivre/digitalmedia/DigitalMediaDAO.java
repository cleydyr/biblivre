package biblivre.digitalmedia;

import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import java.io.File;
import java.util.List;
import java.util.Optional;

public interface DigitalMediaDAO {

    Integer save(MemoryFile file);

    long createOID();

    /**
     * Stores a file restored from a backup and returns where it went. The schema names the library
     * the file belongs to, for stores whose keys carry it.
     */
    StorageLocation importFile(File file, String schema);

    BiblivreFile load(int id, String name);

    /** Looks a row up by id and name together, so ids alone can't be guessed. */
    Optional<StoredMedia> find(int id, String name);

    boolean delete(int id);

    List<DigitalMediaDTO> list();
}

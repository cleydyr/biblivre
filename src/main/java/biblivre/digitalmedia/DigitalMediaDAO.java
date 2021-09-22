package biblivre.digitalmedia;

import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import java.io.File;
import java.util.List;

public interface DigitalMediaDAO {

    Integer save(MemoryFile file);

    long createOID();

    long importFile(File file);

    BiblivreFile load(int id, String name);

    boolean delete(int id);

    List<DigitalMediaDTO> list();
}

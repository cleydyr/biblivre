package biblivre.digitalmedia;

import java.io.File;
import java.util.List;

import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;

public interface IDigitalMediaDAO {

	long createOID();

	Integer save(MemoryFile file);

	long importFile(File file);

	BiblivreFile load(int id, String name);

	boolean delete(int id);

	List<DigitalMediaDTO> list();

}

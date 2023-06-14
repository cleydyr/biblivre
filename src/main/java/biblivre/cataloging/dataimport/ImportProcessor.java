package biblivre.cataloging.dataimport;

import biblivre.cataloging.ImportDTO;
import biblivre.cataloging.RecordDTO;
import java.io.InputStream;
import java.util.function.Function;
import org.marc4j.marc.Record;

public interface ImportProcessor {
    ImportDTO importData(InputStream inputStream, Function<Record, RecordDTO> recordAdapter);
}

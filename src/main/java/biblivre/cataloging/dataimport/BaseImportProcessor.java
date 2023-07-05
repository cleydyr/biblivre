package biblivre.cataloging.dataimport;

import biblivre.cataloging.ImportDTO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.ImportFormat;
import java.io.InputStream;
import java.util.function.Function;
import org.marc4j.MarcReader;
import org.marc4j.marc.Record;

public abstract class BaseImportProcessor implements ImportProcessor {
    protected MarcReader marcReader;

    @Override
    public ImportDTO importData(
            InputStream inputStream, Function<Record, RecordDTO> recordAdapter) {
        initMarcReader(inputStream);

        ImportDTO importDTO = readFromMarcReader(marcReader, recordAdapter);

        importDTO.setFormat(ImportFormat.ISO2709);

        return importDTO;
    }

    private ImportDTO readFromMarcReader(
            MarcReader marcReader, Function<Record, RecordDTO> recordAdapter) {
        ImportDTO dto = new ImportDTO();

        while (marcReader.hasNext()) {
            dto.incrementFound();

            try {
                RecordDTO rdto = recordAdapter.apply(marcReader.next());

                if (rdto != null) {
                    dto.addRecord(rdto);
                    dto.incrementSuccess();
                } else {
                    dto.incrementFailure();
                }
            } catch (Exception e) {
                dto.incrementFailure();
            }
        }

        return dto;
    }

    protected abstract void initMarcReader(InputStream inputStream);
}

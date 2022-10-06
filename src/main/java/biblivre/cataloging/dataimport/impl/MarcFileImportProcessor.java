package biblivre.cataloging.dataimport.impl;

import biblivre.cataloging.dataimport.BaseImportProcessor;
import biblivre.marc.MarcFileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class MarcFileImportProcessor extends BaseImportProcessor {

    @Override
    protected void initMarcReader(InputStream inputStream) {
        marcReader = new MarcFileReader(inputStream, StandardCharsets.UTF_8.name());
    }
}

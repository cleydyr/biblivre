package biblivre.cataloging.dataimport.impl;

import biblivre.cataloging.dataimport.BaseImportProcessor;
import java.io.InputStream;
import org.marc4j.MarcPermissiveStreamReader;
import org.springframework.stereotype.Component;

@Component
public class ISO2709ImportProcessor extends BaseImportProcessor {

    @Override
    protected void initMarcReader(InputStream inputStream) {
        marcReader = new MarcPermissiveStreamReader(inputStream, true, true, "BESTGUESS");
    }
}

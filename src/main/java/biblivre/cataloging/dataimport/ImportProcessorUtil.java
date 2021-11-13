package biblivre.cataloging.dataimport;

import java.util.ServiceLoader;

public class ImportProcessorUtil {
    public static Iterable<ImportProcessor> getImportProcessors() {
        return ServiceLoader.load(ImportProcessor.class);
    }
}

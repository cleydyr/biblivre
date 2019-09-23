package biblivre.administration.reports;

import java.util.Set;
import java.util.TreeMap;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;

public interface ReportsBO {

	DiskFile generateReport(ReportsDTO dto, TranslationsMap i18n);

	TreeMap<String, Set<Integer>> searchAuthors(String author, RecordDatabase database);
}

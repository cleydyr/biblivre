package biblivre.administration.reports;

import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;

public class ReportsBOImpl implements ReportsBO {

	private ReportsDAO dao;

	public ReportsBOImpl(ReportsDAO dao) {
		this.dao = dao;
	}


	public DiskFile generateReport(ReportsDTO dto, TranslationsMap i18n) {
		ReportType type = dto.getType();
		IBiblivreReport report = BiblivreReportFactory.getBiblivreReport(type, dao);
		report.setI18n(i18n);
		report.setSchema(this.getSchema());
		return report.generateReport(dto);
	}


	private String getSchema() {
		return this.dao.getSchema();
	}


	public TreeMap<String, Set<Integer>> searchAuthors(String author, RecordDatabase database) {
		return this.dao.searchAuthors(author, database);
	}
}

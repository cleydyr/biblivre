package biblivre.administration.reports;

import biblivre.cataloging.enums.RecordDatabase;
import biblivre.core.AbstractBO;
import biblivre.core.file.DiskFile;
import biblivre.core.translations.TranslationsMap;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportsBO extends AbstractBO {
    private ReportsDAO reportsDAO;

    private Collection<IBiblivreReport> reports;

    public DiskFile generateReport(ReportsDTO dto, TranslationsMap i18n) {
        ReportType type = dto.getType();

        IBiblivreReport report = getByType(type);

        return report.generateReport(dto, i18n);
    }

    private IBiblivreReport getByType(ReportType type) {
        return reports.stream()
                .filter(report -> report.getReportType().equals(type))
                .findFirst()
                .orElseThrow();
    }

    public TreeMap<String, Set<Integer>> searchAuthors(String author, RecordDatabase database) {
        return this.reportsDAO.searchAuthors(author, database);
    }

    @Autowired
    public void setReportsDAO(ReportsDAO reportsDAO) {
        this.reportsDAO = reportsDAO;
    }

    @Autowired
    public void setReports(Collection<IBiblivreReport> reports) {
        this.reports = reports;
    }
}

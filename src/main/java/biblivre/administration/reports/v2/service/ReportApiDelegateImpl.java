package biblivre.administration.reports.v2.service;

import biblivre.reports.generated.api.ReportApi;
import biblivre.reports.generated.api.ReportApiDelegate;
import biblivre.reports.generated.api.model.Report;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReportApiDelegateImpl implements ReportApiDelegate {
    private ReportService reportService;

    /**
     * GET /report
     *
     * @return The list of report templates available at the instance (status code 200)
     * @see ReportApi#getReports
     */
    public ResponseEntity<List<Report>> getReports() {
        List<biblivre.administration.reports.v2.model.Report> reports = reportService.listReports();

        List<Report> reportsResponse =
                reports.stream().map(ReportApiDelegateImpl::getReport).toList();

        return ResponseEntity.ok(reportsResponse);
    }

    private static Report getReport(biblivre.administration.reports.v2.model.Report report) {
        Report reportResponse = new Report();
        reportResponse.setId(report.getId());
        reportResponse.setName(report.getName());
        reportResponse.setDescription(report.getDescription());
        return reportResponse;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}

package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.reports.generated.api.ReportApiDelegate;
import biblivre.reports.generated.api.model.RestReport;
import biblivre.reports.generated.api.model.RestReportParameter;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportApiDelegateImpl implements ReportApiDelegate {
    private ReportService reportService;

    @Override
    public ResponseEntity<List<RestReport>> getReports() {
        List<biblivre.administration.reports.v2.model.Report> reports = reportService.getReports();

        List<RestReport> restReports =
                reports.stream().map(ReportApiDelegateImpl::getReport).toList();

        return ResponseEntity.ok(restReports);
    }

    @Override
    public ResponseEntity<RestReport> updateReport(Long reportId, RestReport report) {
        var updatedReport =
                reportService.updateReport(reportId, report.getName(), report.getDescription());
        var restReport = toRestReport(updatedReport);

        return ResponseEntity.status(HttpStatus.CREATED).body(restReport);
    }

    @Override
    public ResponseEntity<Void> deleteReport(Long reportId) {
        reportService.deleteReport(reportId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Override
    public ResponseEntity<RestReport> addReport(
            String name,
            Long id,
            String description,
            List<@Valid RestReportParameter> parameters,
            MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Report report = reportService.addReport(inputStream, name, description, file.getSize());

            return ResponseEntity.status(HttpStatus.CREATED).body(toRestReport(report));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static RestReport getReport(biblivre.administration.reports.v2.model.Report report) {
        RestReport reportResponse = new RestReport();
        reportResponse.setId(report.getId());
        reportResponse.setName(report.getName());
        reportResponse.setDescription(report.getDescription());
        return reportResponse;
    }

    private RestReport toRestReport(Report updatedReport) {
        RestReport restReport = new RestReport();
        restReport.setId(updatedReport.getId());
        restReport.setName(updatedReport.getName());
        restReport.setDescription(updatedReport.getDescription());
        restReport.setParameters(toRestReportParameters(updatedReport.getParameters()));
        return restReport;
    }

    private List<RestReportParameter> toRestReportParameters(
            Collection<ReportParameter> parameters) {
        return parameters.stream().map(this::toRestReportParameter).toList();
    }

    private RestReportParameter toRestReportParameter(ReportParameter reportParameter) {
        RestReportParameter restReportParameter = new RestReportParameter();
        restReportParameter.setName(reportParameter.getName());
        restReportParameter.setDescription(reportParameter.getDescription());
        restReportParameter.setType(reportParameter.getType());
        return restReportParameter;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}

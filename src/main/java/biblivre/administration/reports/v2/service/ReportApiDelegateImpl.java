package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.Report;
import biblivre.reports.generated.api.ReportTemplateApiDelegate;
import biblivre.reports.generated.api.model.RestReportTemplate;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportApiDelegateImpl implements ReportTemplateApiDelegate {
    private ReportService reportService;

    @Override
    public ResponseEntity<Void> compileReportTemplate(
            String name, MultipartFile file, String description) {

        try {
            reportService.compileReportTemplate(
                    name, description, file.getInputStream(), file.getSize());
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ReportException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<RestReportTemplate>> getReportTemplates() {
        return new ResponseEntity<>(
                reportService.getReportTemplates().stream()
                        .map(this::toRestReportTemplate)
                        .toList(),
                HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RestReportTemplate> updateReport(
            Long reportTemplateId, RestReportTemplate restReportTemplate) {
        try {
            return new ResponseEntity<>(
                    toRestReportTemplate(
                            reportService.updateReport(
                                    reportTemplateId,
                                    restReportTemplate.getName(),
                                    restReportTemplate.getDescription())),
                    HttpStatus.ACCEPTED);
        } catch (ReportException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<Void> deleteReport(Long reportTemplateId) {
        try {
            reportService.deleteReport(reportTemplateId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ReportException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private RestReportTemplate toRestReportTemplate(Report report) {
        RestReportTemplate restReportTemplate = new RestReportTemplate();
        restReportTemplate.setId(report.getId());
        restReportTemplate.setName(report.getName());
        restReportTemplate.setDescription(report.getDescription());
        return restReportTemplate;
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}

package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.reports.generated.api.ReportTemplateApiDelegate;
import biblivre.reports.generated.api.model.RestReportTemplate;
import biblivre.reports.generated.api.model.RestReportTemplateParameter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ReportApiDelegateImpl implements ReportTemplateApiDelegate {
    private static final Logger logger = LoggerFactory.getLogger(ReportApiDelegateImpl.class);

    private ReportService reportService;

    @Override
    public ResponseEntity<RestReportTemplate> compileReportTemplate(
            String name, MultipartFile file, String description) {

        if (file == null || file.isEmpty()) {
            logger.warn("Report template upload rejected: empty file for template '{}'", name);
            return errorResponse("O arquivo do modelo de relatório está vazio.");
        }

        try {
            Report report =
                    reportService.compileReportTemplate(
                            name, description, file.getInputStream(), file.getSize());
            return new ResponseEntity<>(toRestReportTemplate(report), HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("Failed to read uploaded report template '{}'", name, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ReportException e) {
            logger.error("Failed to compile report template '{}'", name, e);
            return errorResponse(e.getMessage());
        }
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
            logger.error("Failed to update report template {}", reportTemplateId, e);
            return errorResponse(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<Void> deleteReport(Long reportTemplateId) {
        try {
            reportService.deleteReport(reportTemplateId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ReportException e) {
            logger.error("Failed to delete report template {}", reportTemplateId, e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private RestReportTemplate toRestReportTemplate(Report report) {
        RestReportTemplate restReportTemplate = new RestReportTemplate();
        restReportTemplate.setId(report.getId());
        restReportTemplate.setName(report.getName());
        restReportTemplate.setDescription(report.getDescription());

        if (report.getParameters() != null) {
            restReportTemplate.setParameters(
                    report.getParameters().stream().map(this::toRestReportTemplateParameter).toList());
        } else {
            restReportTemplate.setParameters(Collections.emptyList());
        }

        return restReportTemplate;
    }

    private RestReportTemplateParameter toRestReportTemplateParameter(ReportParameter parameter) {
        RestReportTemplateParameter restParameter = new RestReportTemplateParameter();
        restParameter.setName(parameter.getName());
        restParameter.setType(parameter.getType());
        restParameter.setDescription(parameter.getDescription());
        return restParameter;
    }

    @SuppressWarnings("unchecked")
    private static <T> ResponseEntity<T> errorResponse(String message) {
        return (ResponseEntity<T>)
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", message));
    }

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}

package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.JasperReportImpl;
import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.administration.reports.v2.persistence.JasperReportPersistence;
import biblivre.administration.reports.v2.persistence.ReportRepository;
import biblivre.core.SchemaThreadLocal;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is a service for the report module. It is responsible for handling the business logic
 * for the report module.
 */
@Service
public class ReportService {
    private JasperReportPersistence jasperReportPersistence;

    private ReportRepository reportRepository;

    public void compileReportTemplate(
            String name, String description, InputStream reportJRXMLDefinition, long fileSize)
            throws ReportException {
        long digitalMediaId = jasperReportPersistence.compile(reportJRXMLDefinition, fileSize);

        JasperReportImpl compiledReport = jasperReportPersistence.getById(digitalMediaId);

        Collection<JRParameter> jrParameters = compiledReport.getParameters();

        List<ReportParameter> reportParameters =
                jrParameters.stream()
                        .filter(Predicate.not(JRParameter::isSystemDefined))
                        .map(ReportService::toReportParameter)
                        .toList();

        Report report =
                new Report(
                        0,
                        name,
                        description,
                        reportParameters,
                        Collections.emptyList(),
                        SchemaThreadLocal.get(),
                        digitalMediaId);

        reportParameters.forEach(reportParameter -> reportParameter.setReport(report));

        reportRepository.save(report);
    }

    public List<Report> getReportTemplates() {
        List<Report> list = new ArrayList<>();

        for (Report report : reportRepository.findAll()) {
            list.add(report);
        }

        return list;
    }

    public Report updateReport(Long reportTemplateId, String name, String description)
            throws ReportException {
        Report existingReport =
                reportRepository
                        .findById(reportTemplateId)
                        .orElseThrow(() -> new ReportException("Report not found"));

        Report newReport =
                new Report(
                        reportTemplateId,
                        name,
                        description,
                        existingReport.getParameters(),
                        existingReport.getFills(),
                        existingReport.getSchema(),
                        existingReport.getDigitalMediaId());

        return reportRepository.save(newReport);
    }

    public void deleteReport(Long reportTemplateId) throws ReportException {
        reportRepository.deleteById(reportTemplateId);
    }

    private static ReportParameter toReportParameter(JRParameter jrParameter) {
        return new ReportParameter(
                0,
                jrParameter.getName(),
                jrParameter.getValueClassName(),
                jrParameter.getDescription(),
                null);
    }

    @Autowired
    public void setReportPersistence(JasperReportPersistence jasperReportPersistence) {
        this.jasperReportPersistence = jasperReportPersistence;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
}

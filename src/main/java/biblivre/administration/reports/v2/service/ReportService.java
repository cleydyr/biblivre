package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.JasperReportImpl;
import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.administration.reports.v2.persistence.JasperReportPersistence;
import biblivre.administration.reports.v2.persistence.ReportRepository;
import biblivre.core.SchemaThreadLocal;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is a service for the report module. It is responsible for handling the business logic
 * for the report module.
 */
@Service
public class ReportService {
    private DataSource datasource;

    private JasperReportPersistence jasperReportPersistence;

    private ReportRepository reportRepository;

    public void generateReport(
            long reportId, Map<String, String> params, OutputStream consumerOutputStream)
            throws ReportException {
        Report report = reportRepository.findById(reportId).orElseThrow();

        long digitalMediaId = 0; // report.getDigitalMediaId();

        JasperReportImpl jasperReport = jasperReportPersistence.getById(digitalMediaId);

        try (Connection connection = datasource.getConnection()) {
            setSchemaSearchPath(connection);

            Map<String, Object> actualParameters = transformParameters(jasperReport, params);

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport.getJasperReport(), actualParameters, connection);

            JasperExportManager.exportReportToPdfStream(jasperPrint, consumerOutputStream);
        } catch (JRException e) {
            throw new ReportException("can't fill report", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Report> getReports() {
        return StreamSupport.stream(reportRepository.findAll().spliterator(), false).toList();
    }

    public Report updateReport(Long reportId, String title, String description) {
        Report report = reportRepository.findById(reportId).orElseThrow();

        Report updatee =
                new Report(
                        reportId,
                        title,
                        description,
                        report.getParameters(),
                        report.getSchema(),
                        report.getDigitalMediaId());

        return reportRepository.save(updatee);
    }

    public Report deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow();
        reportRepository.deleteById(reportId);
        return report;
    }

    public Report addReport(
            InputStream reportJRXMLDefinition, String title, String description, long size)
            throws ReportException {
        long digitalMediaId = jasperReportPersistence.persist(reportJRXMLDefinition, size);

        JasperReportImpl compiledReport = jasperReportPersistence.getById(digitalMediaId);

        Collection<JRParameter> jrParameters = compiledReport.getParameters();

        List<ReportParameter> reportParameters =
                jrParameters.stream()
                        .filter(Predicate.not(JRParameter::isSystemDefined))
                        .map(ReportService::toReportParameter)
                        .toList();

        Report toBeSaved =
                new Report(
                        0,
                        title,
                        description,
                        reportParameters,
                        SchemaThreadLocal.get(),
                        digitalMediaId);

        reportParameters.forEach(reportParameter -> reportParameter.setReport(toBeSaved));

        return reportRepository.save(toBeSaved);
    }

    private static ReportParameter toReportParameter(JRParameter jrParameter) {
        return new ReportParameter(
                0,
                jrParameter.getName(),
                jrParameter.getValueClassName(),
                jrParameter.getDescription(),
                null);
    }

    private static Map<String, Object> transformParameters(
            JasperReportImpl report, Map<String, String> params) {
        Map<String, Object> actualParameters = new HashMap<>();

        for (JRParameter parameter : report.getParameters()) {
            String parameterName = parameter.getName();
            String parameterValue = params.get(parameterName);
            actualParameters.put(parameterName, parameterValue);
        }

        return actualParameters;
    }

    private static void setSchemaSearchPath(Connection connection) throws SQLException {
        connection
                .createStatement()
                .execute("SET search_path = '" + SchemaThreadLocal.get() + "';");
    }

    @Autowired
    public void setReportPersistence(JasperReportPersistence jasperReportPersistence) {
        this.jasperReportPersistence = jasperReportPersistence;
    }

    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Autowired
    public void setDataSource(DataSource datasource) {
        this.datasource = datasource;
    }
}

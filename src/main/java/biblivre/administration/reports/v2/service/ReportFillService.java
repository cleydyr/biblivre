package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.JasperReportImpl;
import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportFill;
import biblivre.administration.reports.v2.persistence.JasperReportPersistence;
import biblivre.administration.reports.v2.persistence.ReportFillRepository;
import biblivre.administration.reports.v2.persistence.ReportRepository;
import biblivre.core.file.MemoryFile;
import biblivre.digitalmedia.DigitalMediaDAO;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportFillService {
    private ReportRepository reportRepository;

    private ReportFillRepository reportFillRepository;

    private JasperReportPersistence jasperReportPersistence;

    private DigitalMediaDAO digitalMediaDAO;

    private DataSource datasource;

    public ReportFill createReportFill(long reportId, Map<String, String> fillParameters)
            throws ReportException {
        Optional<Report> report = reportRepository.findById(reportId);

        if (report.isEmpty()) {
            throw new IllegalArgumentException("Report not found");
        }

        long digitalMediaId = report.get().getDigitalMediaId();

        JasperReportImpl compileReport = jasperReportPersistence.getById(digitalMediaId);

        try (var connection = datasource.getConnection()) {
            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            compileReport.getJasperReport(),
                            adaptParameters(fillParameters, compileReport.getParameters()),
                            connection);

            byte[] bytes = JasperExportManager.exportReportToPdf(jasperPrint);

            int reportFillDigitalMediaId =
                    digitalMediaDAO.save(
                            new MemoryFile(
                                    "report.pdf",
                                    "application/pdf",
                                    bytes.length,
                                    new ByteArrayInputStream(bytes)));

            ReportFill reportFill =
                    new ReportFill(0, fillParameters, report.get(), reportFillDigitalMediaId);

            return reportFillRepository.save(reportFill);
        } catch (JRException | SQLException e) {
            throw new ReportException("Error filling the report", e);
        }
    }

    private Map<String, Object> adaptParameters(
            Map<String, String> fillParameters, Collection<JRParameter> parameters) {
        return fillParameters.entrySet().stream()
                .collect(
                        java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> {
                                    Optional<JRParameter> parameter =
                                            parameters.stream()
                                                    .filter(p -> p.getName().equals(entry.getKey()))
                                                    .findFirst();

                                    if (parameter.isEmpty()) {
                                        throw new IllegalArgumentException(
                                                "Parameter not found: " + entry.getKey());
                                    }

                                    return convert(
                                            entry.getValue(), parameter.get().getValueClass());
                                }));
    }

    private static <T> T convert(String value, Class<T> clazz) {
        return switch (clazz.getName()) {
            case "java.lang.String" -> clazz.cast(value);
            case "java.lang.Integer" -> clazz.cast(Integer.parseInt(value));
            case "java.lang.Long" -> clazz.cast(Long.parseLong(value));
            case "java.lang.Float" -> clazz.cast(Float.parseFloat(value));
            case "java.lang.Double" -> clazz.cast(Double.parseDouble(value));
            case "java.lang.Boolean" -> clazz.cast(Boolean.parseBoolean(value));
            case "java.time.LocalDate" -> clazz.cast(java.time.LocalDate.parse(value));
            case "java.time.LocalDateTime" -> clazz.cast(java.time.LocalDateTime.parse(value));
            default ->
                    throw new IllegalArgumentException(
                            "Unsupported parameter type: " + clazz.getName());
        };
    }

    // Autowired methods
    @Autowired
    public void setReportRepository(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Autowired
    public void setReportFillRepository(ReportFillRepository reportFillRepository) {
        this.reportFillRepository = reportFillRepository;
    }

    @Autowired
    public void setJasperReportPersistence(JasperReportPersistence jasperReportPersistence) {
        this.jasperReportPersistence = jasperReportPersistence;
    }

    @Autowired
    public void setDigitalMediaDAO(DigitalMediaDAO digitalMediaDAO) {
        this.digitalMediaDAO = digitalMediaDAO;
    }

    @Autowired
    public void setDatasource(DataSource datasource) {
        this.datasource = datasource;
    }
}

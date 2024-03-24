package biblivre.administration.reports.v2.model;

import java.util.Arrays;
import java.util.Collection;
import lombok.Getter;
import net.sf.jasperreports.engine.JRParameter;

@Getter
public class JasperReportImpl implements JasperReport {
    private final long id;

    private final String name;
    private final String description;
    private final Collection<JRParameter> parameters;

    private final net.sf.jasperreports.engine.JasperReport jasperReport;

    public JasperReportImpl(long id, net.sf.jasperreports.engine.JasperReport jasperReport) {
        this.jasperReport = jasperReport;
        this.id = id;
        this.name = jasperReport.getName();
        this.description = jasperReport.getProperty("description");
        this.parameters = Arrays.asList(jasperReport.getParameters());
    }
}

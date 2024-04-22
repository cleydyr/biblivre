package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.JasperReportImpl;
import java.io.InputStream;
import java.util.Collection;

public interface JasperReportPersistence {
    JasperReportImpl getById(long id) throws ReportException;

    Collection<JasperReportImpl> listReports() throws Exception;

    long compile(InputStream reportJRXMLDefinition, long size) throws ReportException;
}

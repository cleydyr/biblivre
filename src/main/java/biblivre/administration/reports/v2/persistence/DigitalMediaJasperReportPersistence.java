package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.JasperReportImpl;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;
import biblivre.digitalmedia.DigitalMediaDAO;
import java.io.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DigitalMediaJasperReportPersistence implements JasperReportPersistence {
    private DigitalMediaDAO digitalMediaDAO;

    @Override
    public JasperReportImpl getById(long id) throws ReportException {
        try (BiblivreFile biblivreFile = digitalMediaDAO.load((int) id, "report.jrxml")) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            biblivreFile.copy(outputStream);

            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            JasperReport report = (JasperReport) JRLoader.loadObject(inputStream);

            return new JasperReportImpl(id, report);

        } catch (Exception e) {
            throw new ReportException("can't load report", e);
        }
    }

    @Override
    public Collection<JasperReportImpl> listReports() throws Exception {
        var e = new AtomicReference<Exception>(null);

        var reports =
                digitalMediaDAO.list().stream()
                        .filter(file -> file.getName().equals("report.jrxml"))
                        .map(
                                file -> {
                                    try {
                                        return getById(file.getId());
                                    } catch (ReportException reportException) {
                                        e.set(reportException);

                                        return null;
                                    }
                                })
                        .toList();

        if (e.get() != null) {
            throw e.get();
        }

        return reports;
    }

    @Override
    public long compile(InputStream reportJRXMLDefinition, long size) throws ReportException {
        ByteArrayOutputStream compiledReportOutputStream = new ByteArrayOutputStream();

        try {
            JasperCompileManager.compileReportToStream(
                    reportJRXMLDefinition, compiledReportOutputStream);

            InputStream compiledReportInputStream =
                    new ByteArrayInputStream(compiledReportOutputStream.toByteArray());

            return (long)
                    digitalMediaDAO.save(
                            new MemoryFile(
                                    "report.jrxml",
                                    "application/octet-stream",
                                    compiledReportInputStream.available(),
                                    compiledReportInputStream));
        } catch (Exception e) {
            throw new ReportException("can't compile report", e);
        }
    }

    @Autowired
    public void setDigitalMediaDAO(DigitalMediaDAO digitalMediaDAO) {
        this.digitalMediaDAO = digitalMediaDAO;
    }
}

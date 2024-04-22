package biblivre.administration.reports.v2.persistence;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.core.SchemaThreadLocal;
import biblivre.digitalmedia.BaseDigitalMediaDAO;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class DigitalMediaJasperReportPersistenceTest extends AbstractContainerDatabaseTest {

    @Autowired private BaseDigitalMediaDAO dao;

    private static final String minimalJrxmlReport =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="jasperReport" pageWidth="595" pageHeight="842" columnWidth="555" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" uuid="f3e3f3e3-3e3e-3e3e-3e3e-3e3e3e3e3e3e">
                        <queryString>
                            <![CDATA[]]>
                        </queryString>
                        <title>
                            <band height="79" splitType="Stretch">
                                <staticText>
                                    <reportElement x="0" y="0" width="555" height="30" uuid="f3e3f3e3-3e3e-3e3e-3e3e-3e3e3e3e3e3e"/>
                                    <textElement textAlignment="Center" verticalAlignment="Middle"/>
                                    <text><![CDATA[Report Title]]></text>
                                </staticText>
                            </band>
                        </title>
                    </jasperReport>
                    """;

    @AfterEach
    void cleanUp() {
        SchemaThreadLocal.setSchema("single");

        var reports = dao.list();

        for (var jasperReport : reports) {
            dao.delete(jasperReport.getId());
        }
    }

    @Test
    void getById() throws ReportException {
        SchemaThreadLocal.setSchema("single");

        DigitalMediaJasperReportPersistence digitalMediaReportPersistence =
                new DigitalMediaJasperReportPersistence();

        digitalMediaReportPersistence.setDigitalMediaDAO(dao);

        long id =
                digitalMediaReportPersistence.compile(
                        new ByteArrayInputStream(minimalJrxmlReport.getBytes()),
                        minimalJrxmlReport.length());

        assertDoesNotThrow(() -> digitalMediaReportPersistence.getById(id));

        assertThrows(Exception.class, () -> digitalMediaReportPersistence.getById(0));

        try {
            var jasperReport = digitalMediaReportPersistence.getById(id);

            assertNotNull(jasperReport);

            assertEquals("jasperReport", jasperReport.getName());
        } catch (ReportException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listReports() throws ReportException {
        SchemaThreadLocal.setSchema("single");

        DigitalMediaJasperReportPersistence digitalMediaReportPersistence =
                new DigitalMediaJasperReportPersistence();

        digitalMediaReportPersistence.setDigitalMediaDAO(dao);

        long id =
                digitalMediaReportPersistence.compile(
                        new ByteArrayInputStream(minimalJrxmlReport.getBytes()),
                        minimalJrxmlReport.length());

        assertDoesNotThrow(digitalMediaReportPersistence::listReports);

        try {
            var reports = digitalMediaReportPersistence.listReports();

            assertNotNull(reports);

            assertEquals(1, reports.size());

            var jasperReport = reports.iterator().next();

            assertEquals("jasperReport", jasperReport.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

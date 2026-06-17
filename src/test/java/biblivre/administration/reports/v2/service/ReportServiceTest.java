package biblivre.administration.reports.v2.service;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.administration.reports.v2.persistence.ReportRepository;
import biblivre.core.SchemaThreadLocal;
import biblivre.digitalmedia.BaseDigitalMediaDAO;
import java.io.ByteArrayInputStream;
import java.util.List;
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
class ReportServiceTest extends AbstractContainerDatabaseTest {

    @Autowired private ReportService reportService;

    @Autowired private ReportRepository reportRepository;

    @Autowired private BaseDigitalMediaDAO digitalMediaDAO;

    private static final String jrxmlWithParameters =
            """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd" name="parameterizedReport" pageWidth="595" pageHeight="842" columnWidth="555" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20" uuid="a1a1a1a1-a1a1-a1a1-a1a1-a1a1a1a1a1a1">
                        <parameter name="dataInicio" class="java.time.LocalDate">
                            <parameterDescription><![CDATA[Data inicial]]></parameterDescription>
                        </parameter>
                        <parameter name="ativo" class="java.lang.Boolean">
                            <parameterDescription><![CDATA[Registros ativos]]></parameterDescription>
                        </parameter>
                        <queryString>
                            <![CDATA[]]>
                        </queryString>
                        <title>
                            <band height="79" splitType="Stretch">
                                <staticText>
                                    <reportElement x="0" y="0" width="555" height="30" uuid="b2b2b2b2-b2b2-b2b2-b2b2-b2b2b2b2b2b2"/>
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

        reportRepository.deleteAll();

        for (var media : digitalMediaDAO.list()) {
            digitalMediaDAO.delete(media.getId());
        }
    }

    @Test
    void compileReportTemplate_extractsAndPersistsParameters() throws Exception {
        SchemaThreadLocal.setSchema("single");

        Report report =
                reportService.compileReportTemplate(
                        "Parameterized report",
                        "Report with parameters",
                        new ByteArrayInputStream(jrxmlWithParameters.getBytes()),
                        jrxmlWithParameters.length());

        assertNotEquals(0, report.getId());
        assertEquals("Parameterized report", report.getName());
        assertEquals(2, report.getParameters().size());

        List<ReportParameter> parameters = report.getParameters().stream().toList();

        ReportParameter startDate =
                parameters.stream()
                        .filter(parameter -> "dataInicio".equals(parameter.getName()))
                        .findFirst()
                        .orElseThrow();

        assertEquals("java.time.LocalDate", startDate.getType());
        assertEquals("Data inicial", startDate.getDescription());

        ReportParameter active =
                parameters.stream()
                        .filter(parameter -> "ativo".equals(parameter.getName()))
                        .findFirst()
                        .orElseThrow();

        assertEquals("java.lang.Boolean", active.getType());
        assertEquals("Registros ativos", active.getDescription());

        Report persisted = reportRepository.findByIdWithParameters(report.getId()).orElseThrow();

        assertEquals(2, persisted.getParameters().size());
    }
}

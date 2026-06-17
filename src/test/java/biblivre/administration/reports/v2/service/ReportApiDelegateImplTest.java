package biblivre.administration.reports.v2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.reports.generated.api.model.RestReportTemplate;
import biblivre.reports.generated.api.model.RestReportTemplateParameter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ReportApiDelegateImplTest {

    @Test
    void getReportTemplates_mapsParameters() {
        ReportParameter parameter =
                new ReportParameter(1, "dataInicio", "java.time.LocalDate", "Data inicial", null);
        Report report =
                new Report(
                        42,
                        "Test report",
                        "Description",
                        List.of(parameter),
                        List.of(),
                        "single",
                        99);

        ReportService reportService = mock(ReportService.class);
        when(reportService.getReportTemplates()).thenReturn(List.of(report));

        ReportApiDelegateImpl delegate = new ReportApiDelegateImpl();
        delegate.setReportService(reportService);

        ResponseEntity<List<RestReportTemplate>> response = delegate.getReportTemplates();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        RestReportTemplate template = response.getBody().getFirst();
        assertEquals(42, template.getId());
        assertEquals("Test report", template.getName());
        assertEquals(1, template.getParameters().size());

        RestReportTemplateParameter restParameter = template.getParameters().getFirst();
        assertEquals("dataInicio", restParameter.getName());
        assertEquals("java.time.LocalDate", restParameter.getType());
        assertEquals("Data inicial", restParameter.getDescription());
    }

    @Test
    void getReportTemplates_returnsEmptyParametersWhenReportHasNone() {
        Report report =
                new Report(7, "No params", "Description", List.of(), List.of(), "single", 10);

        ReportService reportService = mock(ReportService.class);
        when(reportService.getReportTemplates()).thenReturn(List.of(report));

        ReportApiDelegateImpl delegate = new ReportApiDelegateImpl();
        delegate.setReportService(reportService);

        ResponseEntity<List<RestReportTemplate>> response = delegate.getReportTemplates();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().getFirst().getParameters().isEmpty());
    }
}

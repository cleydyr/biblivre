package biblivre.administration.reports.v2.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportFill;
import biblivre.reports.generated.api.model.RestReportFill;
import biblivre.reports.generated.api.model.RestReportFillRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ReportFillApiDelegateImplTest {

    @Test
    void createReportFill_includesSchemaInDownloadUri() throws Exception {
        Report report =
                new Report(42, "Test report", "Description", List.of(), List.of(), "single", 99);
        ReportFill reportFill = new ReportFill(1, Map.of("dataInicio", "2024-01-01"), report, 7);

        ReportFillService reportFillService = mock(ReportFillService.class);
        when(reportFillService.createReportFill(anyLong(), org.mockito.ArgumentMatchers.anyMap()))
                .thenReturn(reportFill);

        ReportFillApiDelegateImpl delegate = new ReportFillApiDelegateImpl();
        delegate.setReportService(reportFillService);

        RestReportFillRequest request = new RestReportFillRequest();
        request.setReportTemplateId(42L);
        request.setParameters(Map.of("dataInicio", "2024-01-01"));

        ResponseEntity<RestReportFill> response = delegate.createReportFill(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(
                "single/DigitalMediaController?id=NzpyZXBvcnQucGRm",
                response.getBody().getUri().toString());
    }
}

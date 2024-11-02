package biblivre.administration.reports.v2.service;

import biblivre.administration.reports.v2.model.ReportFill;
import biblivre.digitalmedia.DigitalMediaEncodingUtil;
import biblivre.reports.generated.api.ReportFillApiDelegate;
import biblivre.reports.generated.api.model.RestReportFill;
import biblivre.reports.generated.api.model.RestReportFillRequest;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReportFillApiDelegateImpl implements ReportFillApiDelegate {
    private ReportFillService reportService;

    @Override
    public ResponseEntity<RestReportFill> createReportFill(
            RestReportFillRequest restReportFillRequest) {
        var reportTemplateId = restReportFillRequest.getReportTemplateId();

        var fillParameters = restReportFillRequest.getParameters();

        try {
            var reportFill = reportService.createReportFill(reportTemplateId, fillParameters);

            return ResponseEntity.ok(toRestReportFill(reportFill));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private static RestReportFill toRestReportFill(ReportFill reportFill)
            throws URISyntaxException {
        var restReportFill = new RestReportFill();

        restReportFill.setId(reportFill.getId());

        restReportFill.setReportTemplateId(reportFill.getReport().getId());

        String digitalMediaId =
                DigitalMediaEncodingUtil.getEncodedId(reportFill.getDigitalMediaId(), "report.pdf");

        restReportFill.setUri(new URI("DigitalMediaController?id=" + digitalMediaId));

        return restReportFill;
    }

    @Autowired
    public void setReportService(ReportFillService reportService) {
        this.reportService = reportService;
    }
}

package biblivre.administration.reports.v2.web;

/**
 * This class is a controller for the report module. It is responsible for handling the requests and
 * responses for the report module. Tasks for which it is responsible include: - Listing the
 * available reports - Generating reports in different formats
 */
import biblivre.administration.reports.v2.exception.ReportException;
import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v2/reports")
public class ReportController {
    private ReportService reportService;

    @GetMapping("/list")
    public Iterable<Report> list() throws Exception {
        return reportService.listReports();
    }

    @GetMapping("/generate/{id}")
    public void generate(
            HttpServletResponse response,
            @PathVariable long id,
            @RequestParam Map<String, String> params)
            throws IOException, ReportException {
        reportService.generateReport(id, params, response.getOutputStream());
    }

    @PostMapping("/add")
    public long add(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description)
            throws ReportException, IOException {
        return reportService.persistReport(
                file.getInputStream(), title, description, file.getSize());
    }

    @PutMapping("/update")
    public void update() {}

    @Autowired
    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    // TODO: handle exceptions
}

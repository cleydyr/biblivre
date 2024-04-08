package biblivre.swagger.api;

import biblivre.swagger.model.NameAndDescription;
import biblivre.swagger.model.Report;
import biblivre.swagger.model.ReportFill;
import biblivre.swagger.model.ReportFillParameter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@jakarta.annotation.Generated(
        value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
        date = "2024-04-08T11:39:25.814969055Z[GMT]")
@RestController
public class ReportApiController implements ReportApi {

    private static final Logger log = LoggerFactory.getLogger(ReportApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public ReportApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Report> addReport() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Report>(
                        objectMapper.readValue("\"\"", Report.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Report>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Report>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> deleteFills(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> deleteReport(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> deleteReportFill(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId,
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportFillId")
                    Long reportFillId) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<ReportFill> fillReport(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId,
            @Parameter(
                            in = ParameterIn.DEFAULT,
                            description = "",
                            required = true,
                            schema = @Schema())
                    @Valid
                    @RequestBody
                    List<ReportFillParameter> body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<ReportFill>(
                        objectMapper.readValue("\"\"", ReportFill.class),
                        HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<ReportFill>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<ReportFill>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Report>> getReport(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Report>>(
                        objectMapper.readValue("[ \"\", \"\" ]", List.class),
                        HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Report>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Object>> getReportFill(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId,
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportFillId")
                    Long reportFillId) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Object>>(
                        objectMapper.readValue("[ \"\", \"\" ]", List.class),
                        HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Object>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Object>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Report>> getReports() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Report>>(
                        objectMapper.readValue("[ \"\", \"\" ]", List.class),
                        HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Report>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Report>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Report> updateReport(
            @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema())
                    @PathVariable("reportId")
                    Long reportId,
            @Parameter(
                            in = ParameterIn.DEFAULT,
                            description = "",
                            required = true,
                            schema = @Schema())
                    @Valid
                    @RequestBody
                    NameAndDescription body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Report>(
                        objectMapper.readValue("\"\"", Report.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Report>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Report>(HttpStatus.NOT_IMPLEMENTED);
    }
}

package biblivre.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

/** ReportFill */
@Validated
@jakarta.annotation.Generated(
        value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
        date = "2024-04-08T11:39:25.814969055Z[GMT]")
public class ReportFill extends Identifiable {
    @JsonProperty("reportId")
    private Long reportId = null;

    @JsonProperty("fillParameters")
    @Valid
    private List<ReportFillParameter> fillParameters = new ArrayList<ReportFillParameter>();

    public ReportFill reportId(Long reportId) {
        this.reportId = reportId;
        return this;
    }

    /**
     * Get reportId
     *
     * @return reportId
     */
    @Schema(example = "42", description = "")
    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public ReportFill fillParameters(List<ReportFillParameter> fillParameters) {
        this.fillParameters = fillParameters;
        return this;
    }

    public ReportFill addFillParametersItem(ReportFillParameter fillParametersItem) {
        this.fillParameters.add(fillParametersItem);
        return this;
    }

    /**
     * Get fillParameters
     *
     * @return fillParameters
     */
    @Schema(
            example = "[{\"parameterName\":\"dataInicio\",\"parameterValue\":\"1986-03-12\"}]",
            required = true,
            description = "")
    @NotNull
    @Valid
    public List<ReportFillParameter> getFillParameters() {
        return fillParameters;
    }

    public void setFillParameters(List<ReportFillParameter> fillParameters) {
        this.fillParameters = fillParameters;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReportFill reportFill = (ReportFill) o;
        return Objects.equals(this.reportId, reportFill.reportId)
                && Objects.equals(this.fillParameters, reportFill.fillParameters)
                && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, fillParameters, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReportFill {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    reportId: ").append(toIndentedString(reportId)).append("\n");
        sb.append("    fillParameters: ").append(toIndentedString(fillParameters)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first
     * line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

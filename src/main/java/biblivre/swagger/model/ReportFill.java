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
        date = "2024-04-07T11:01:49.151127865Z[GMT]")
public class ReportFill {
    @JsonProperty("reportId")
    private Integer reportId = null;

    @JsonProperty("fillParameters")
    @Valid
    private List<ReportFillParameter> fillParameters = new ArrayList<ReportFillParameter>();

    public ReportFill reportId(Integer reportId) {
        this.reportId = reportId;
        return this;
    }

    /**
     * Get reportId
     *
     * @return reportId
     */
    @Schema(example = "42", description = "")
    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
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
                && Objects.equals(this.fillParameters, reportFill.fillParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reportId, fillParameters);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReportFill {\n");

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

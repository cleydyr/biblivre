package biblivre.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/** InlineResponse404 */
@Validated
@jakarta.annotation.Generated(
        value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
        date = "2024-04-07T11:01:49.151127865Z[GMT]")
public class InlineResponse404 {
    @JsonProperty("statusCode")
    private Integer statusCode = null;

    @JsonProperty("error")
    private InlineResponse404Error error = null;

    public InlineResponse404 statusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Get statusCode
     *
     * @return statusCode
     */
    @Schema(example = "404", description = "")
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public InlineResponse404 error(InlineResponse404Error error) {
        this.error = error;
        return this;
    }

    /**
     * Get error
     *
     * @return error
     */
    @Schema(description = "")
    @Valid
    public InlineResponse404Error getError() {
        return error;
    }

    public void setError(InlineResponse404Error error) {
        this.error = error;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InlineResponse404 inlineResponse404 = (InlineResponse404) o;
        return Objects.equals(this.statusCode, inlineResponse404.statusCode)
                && Objects.equals(this.error, inlineResponse404.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, error);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class InlineResponse404 {\n");

        sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
        sb.append("    error: ").append(toIndentedString(error)).append("\n");
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

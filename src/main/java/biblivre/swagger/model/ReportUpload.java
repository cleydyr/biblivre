package biblivre.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

/** ReportUpload */
@Validated
@jakarta.annotation.Generated(
        value = "io.swagger.codegen.v3.generators.java.SpringCodegen",
        date = "2024-04-08T11:39:25.814969055Z[GMT]")
public class ReportUpload extends Report {
    @JsonProperty("file")
    private Resource file = null;

    public ReportUpload file(Resource file) {
        this.file = file;
        return this;
    }

    /**
     * Get file
     *
     * @return file
     */
    @Schema(description = "")
    @Valid
    public Resource getFile() {
        return file;
    }

    public void setFile(Resource file) {
        this.file = file;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReportUpload reportUpload = (ReportUpload) o;
        return Objects.equals(this.file, reportUpload.file) && super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, super.hashCode());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ReportUpload {\n");
        sb.append("    ").append(toIndentedString(super.toString())).append("\n");
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
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

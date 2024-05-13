package biblivre.administration.reports.v2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "global")
@Getter
@AllArgsConstructor
public class ReportFill {
    public ReportFill() {}

    @GeneratedValue @Id long id;

    @ElementCollection
    @CollectionTable(
            name = "report_fill_parameters",
            joinColumns = @JoinColumn(name = "report_fill_id"))
    @MapKeyColumn(name = "parameter_name")
    @Column(name = "parameter_value")
    Map<String, String> fillParameters;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    @JsonBackReference
    @Setter
    Report report;

    int digitalMediaId;
}

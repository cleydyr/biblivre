package biblivre.administration.reports.v2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

/**
 * A report parameter is a parameter that is used to generate a report. It contains a name and a
 * type.
 */
@Entity
@Table(name = "report_parameters", schema = "global")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReportParameter {
    @GeneratedValue @Id long id;

    String name;

    String type;

    String description;

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    @JsonBackReference
    @Setter
    Report report;
}

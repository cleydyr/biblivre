package biblivre.administration.reports.v2.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(schema = "global")
@Getter
@AllArgsConstructor
public class Report {
    public Report() {}

    @GeneratedValue @Id long id;

    String name;

    String description;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    @JsonManagedReference
    Collection<ReportParameter> parameters;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL)
    @JsonManagedReference
    Collection<ReportFill> fills;

    String schema;

    long digitalMediaId;
}

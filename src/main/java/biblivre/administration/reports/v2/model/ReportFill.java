package biblivre.administration.reports.v2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Table(schema = "global")
@Getter
@AllArgsConstructor
public class ReportFill {
    public ReportFill() {}

    @GeneratedValue @Id long id;

    long reportId;

    String fillParameters;

    String url;
}

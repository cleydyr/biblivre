package biblivre.administration.reports.v2.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A report parameter is a parameter that is used to generate a report. It contains a name and a
 * type.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReportParameter {
    @Setter long id;

    String name;

    String type;

    String description;

    @Setter Report report;
}

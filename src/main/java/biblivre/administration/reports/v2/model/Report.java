package biblivre.administration.reports.v2.model;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Report {
    public Report() {}

    @Setter long id;

    String name;

    String description;

    Collection<ReportParameter> parameters;

    Collection<ReportFill> fills;

    String schema;

    long digitalMediaId;
}

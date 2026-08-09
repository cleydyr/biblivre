package biblivre.administration.reports.v2.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ReportFill {
    public ReportFill() {}

    @Setter long id;

    Map<String, String> fillParameters;

    @Setter Report report;

    int digitalMediaId;
}

package biblivre.administration.reports;

import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class TopographicReport extends BaseAssetHoldingReport {
    @Override
    public ReportType getReportType() {
        return ReportType.TOPOGRAPHIC_FULL;
    }

    @Override
    protected String getTitle() {
        return this.getText("administration.reports.title.topographic");
    }

    @Override
    protected Comparator<String[]> getComparator() {
        return (o1, o2) -> Comparator.<String>naturalOrder().compare(o1[1], o2[1]);
    }
}

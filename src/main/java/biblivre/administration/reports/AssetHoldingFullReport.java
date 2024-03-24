package biblivre.administration.reports;

import java.util.Comparator;
import org.springframework.stereotype.Component;

@Component
public class AssetHoldingFullReport extends BaseAssetHoldingReport {

    @Override
    public ReportType getReportType() {
        return ReportType.ASSET_HOLDING_FULL;
    }

    @Override
    protected String getTitle() {
        return this.getText("administration.reports.title.holdings_full");
    }

    @Override
    protected Comparator<String[]> getComparator() {
        return (o1, o2) -> Comparator.<String>naturalOrder().compare(o1[1], o2[1]);
    }
}

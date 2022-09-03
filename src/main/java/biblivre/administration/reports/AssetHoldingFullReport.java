package biblivre.administration.reports;

import biblivre.core.utils.NaturalOrderComparator;
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
    protected int doCompare(String[] o1, String[] o2) {
        return NaturalOrderComparator.NUMERICAL_ORDER.compare(o1[1], o2[1]);
    }
}

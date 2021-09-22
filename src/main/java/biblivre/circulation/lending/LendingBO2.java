package biblivre.circulation.lending;

import biblivre.cataloging.holding.HoldingDTO;
import biblivre.core.AbstractBO;

public class LendingBO2 extends AbstractBO {
    protected LendingDAO lendingDAO;

    public LendingDTO getCurrentLending(HoldingDTO holding) {
        return this.lendingDAO.getCurrentLending(holding);
    }

    public Integer countLentHoldings(int recordId) {
        return this.lendingDAO.countLentHoldings(recordId);
    }

	public void setLendingDAO(LendingDAO lendingDAO) {
		this.lendingDAO = lendingDAO;
	}
}

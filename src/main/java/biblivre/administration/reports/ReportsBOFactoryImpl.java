package biblivre.administration.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportsBOFactoryImpl implements ReportsBOFactory {

	private ReportsDAOFactory reportsDAOFactory;

	@Autowired
	public ReportsBOFactoryImpl(ReportsDAOFactory reportsDAOFactory) {
		this.reportsDAOFactory = reportsDAOFactory;
	}


	@Override
	public ReportsBO getInstance(String schema) {
		ReportsBO reportsBOImpl = new ReportsBOImpl(reportsDAOFactory.getInstance(schema));

		return reportsBOImpl;
	}
}

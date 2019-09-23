package biblivre.administration.reports;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import biblivre.administration.reports.configuration.ReportsConfiguration;

@Service
public class ReportsBOFactoryImpl implements ReportsBOFactory {

	private ReportsDAOFactory reportsDAOFactory;

	@Autowired
	public ReportsBOFactoryImpl(ReportsDAOFactory reportsDAOFactory) {
		super();
		this.reportsDAOFactory = reportsDAOFactory;
	}


	@Override
	public ReportsBO getInstance(String schema) {
		ReportsBO reportsBOImpl = new ReportsBOImpl(reportsDAOFactory.getInstance(schema));

		return reportsBOImpl;
	}
}

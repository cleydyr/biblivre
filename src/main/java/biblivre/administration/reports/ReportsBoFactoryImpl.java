package biblivre.administration.reports;

import org.springframework.stereotype.Service;

@Service
public class ReportsBoFactoryImpl implements ReportsBOFactory {

	@Override
	public ReportsBO getInstance(String schema) {
		ReportsBO reportsBOImpl = new ReportsBOImpl(ReportsDAO.getInstance(schema), schema);

		return reportsBOImpl;
	}

}

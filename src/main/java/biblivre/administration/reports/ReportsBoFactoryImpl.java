package biblivre.administration.reports;

import org.springframework.stereotype.Service;

@Service
public class ReportsBoFactoryImpl implements ReportsBOFactory {

	@Override
	public ReportsBOImpl getInstance(String schema) {
		ReportsBOImpl reportsBOImpl = new ReportsBOImpl(ReportsDAO.getInstance(schema), schema);

		return reportsBOImpl;
	}

}

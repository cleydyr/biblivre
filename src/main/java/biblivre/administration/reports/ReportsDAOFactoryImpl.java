package biblivre.administration.reports;

public class ReportsDAOFactoryImpl implements ReportsDAOFactory {
	public ReportsDAO getInstance(String schema) {
		return new ReportsDAOImpl(schema);
	}
}

package biblivre.administration.reports;

public interface ReportsDAOFactory {

	ReportsDAO getInstance(String dataSourceName, String schema);

	ReportsDAO getInstance(String schema);

}
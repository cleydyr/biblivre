package biblivre.administration.reports;

public interface ReportsDAOFactory {

	ReportsDAO getInstance(String schema);

}

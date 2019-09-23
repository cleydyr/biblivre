package biblivre.administration.reports;

import biblivre.core.utils.Constants;

public class ReportsDAOFactoryImpl implements ReportsDAOFactory {
	public ReportsDAO getInstance(String dataSourceName, String schema) {
		return new ReportsDAOImpl(schema, dataSourceName);
	}

	public ReportsDAO getInstance(String schema) {
		return new ReportsDAOImpl(Constants.DEFAULT_DATASOURCE_NAME, schema);
	}
}

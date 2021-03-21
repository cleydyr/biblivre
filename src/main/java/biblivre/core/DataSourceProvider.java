package biblivre.core;

import javax.sql.DataSource;

public interface DataSourceProvider {

    DataSource getDataSource(String databaseName);
}

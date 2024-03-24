package biblivre;

import biblivre.core.DataSourceProvider;
import biblivre.core.HikariDataSourceProvider;
import biblivre.core.utils.DatabaseUtils;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class DataSourceConfig {
    @Bean
    public DataSource getDataSource() {
        DataSourceProvider dataSourceProvider = new HikariDataSourceProvider();

        DataSource datasource = dataSourceProvider.getDataSource(DatabaseUtils.getDatabaseName());

        return new SchemaAwareDatasourceDecorator(datasource);
    }
}

package biblivre;

import biblivre.core.HikariDataSourceProvider;
import biblivre.core.utils.DatabaseUtils;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@Slf4j
public class DataSourceConfig {
    private final HikariDataSourceProvider dataSourceProvider = new HikariDataSourceProvider();

    @Bean
    public DataSource getDataSource() {
        DataSource datasource = dataSourceProvider.getDataSource(DatabaseUtils.getDatabaseName());

        return new SchemaAwareDatasourceDecorator(datasource);
    }

    @PreDestroy
    public void closeDataSource() {
        HikariDataSource dataSource =
                dataSourceProvider.getDataSource(DatabaseUtils.getDatabaseName());

        dataSource.close();
    }
}

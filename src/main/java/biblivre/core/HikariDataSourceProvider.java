package biblivre.core;

import biblivre.core.utils.DatabaseUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.PrintWriter;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGSimpleDataSource;

@Slf4j
public class HikariDataSourceProvider implements DataSourceProvider {
    private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%s/%s";

    private HikariDataSource hikariDataSource;

    @Override
    public HikariDataSource getDataSource(String databaseName) {
        if (this.hikariDataSource != null) {
            if (log.isInfoEnabled()) {
                log.info("Returning existing HikariDataSource");
            }

            return this.hikariDataSource;
        }

        Properties props = new Properties();

        props.setProperty("dataSourceClassName", PGSimpleDataSource.class.getName());

        props.setProperty("dataSource.user", DatabaseUtils.getDatabaseUsername());

        props.setProperty("dataSource.password", DatabaseUtils.getDatabasePassword());

        props.setProperty(
                "dataSource.url",
                String.format(
                        JDBC_URL_TEMPLATE,
                        DatabaseUtils.getDatabaseHostName(),
                        DatabaseUtils.getDatabasePort(),
                        databaseName));

        props.setProperty("maximumPoolSize", "10");

        props.setProperty("minimumIdle", "5");

        props.put("dataSource.logWriter", new PrintWriter(System.out));

        this.hikariDataSource = new HikariDataSource(new HikariConfig(props));

        return this.hikariDataSource;
    }
}

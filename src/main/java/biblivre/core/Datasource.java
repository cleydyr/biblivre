package biblivre.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Datasource {
	private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;
    private static final String SET_SCHEMA_STRING = "SET search_path = '%s', public, pg_catalog;";

    static {
        config.setJdbcUrl( "jdbc:postgresql://127.0.0.1:5432/biblivre4" );
        config.setUsername( "biblivre" );
        config.setPassword( "abracadabra" );
        config.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);

        ds = new HikariDataSource( config );
    }

    private Datasource() {}
    
    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static Connection getConnection(String schema) throws SQLException {
    	Connection connection = getConnection();

    	if (schema != null) {
			try (Statement createStatement = connection.createStatement()) {
				createStatement.execute(String.format(SET_SCHEMA_STRING, schema));
			}
		}

		return connection;
    }
}

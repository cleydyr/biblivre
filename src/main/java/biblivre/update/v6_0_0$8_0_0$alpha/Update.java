package biblivre.update.v6_0_0$8_0_0$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    @Override
    public void doUpdate(Connection connection) {
        createReportTables(connection);
    }

    private void createReportTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS global.report (
                        id BIGSERIAL PRIMARY KEY,
                        name TEXT,
                        description TEXT,
                        schema TEXT,
                        digital_media_id BIGINT NOT NULL
                    )
                    """);
            statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS global.report_parameters (
                        id BIGSERIAL PRIMARY KEY,
                        name TEXT,
                        type TEXT,
                        description TEXT,
                        report_id BIGINT NOT NULL REFERENCES global.report (id) ON DELETE CASCADE
                    )
                    """);
            statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS global.report_fill (
                        id BIGSERIAL PRIMARY KEY,
                        report_id BIGINT NOT NULL REFERENCES global.report (id) ON DELETE CASCADE,
                        digital_media_id INTEGER NOT NULL
                    )
                    """);
            statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS global.report_fill_parameters (
                        report_fill_id BIGINT NOT NULL
                            REFERENCES global.report_fill (id) ON DELETE CASCADE,
                        parameter_name TEXT NOT NULL,
                        parameter_value TEXT,
                        PRIMARY KEY (report_fill_id, parameter_name)
                    )
                    """);
        } catch (Exception e) {
            throw new UpdateException("Error creating report tables", e);
        }
    }
}

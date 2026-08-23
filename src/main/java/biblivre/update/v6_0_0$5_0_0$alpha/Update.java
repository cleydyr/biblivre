package biblivre.update.v6_0_0$5_0_0$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    @Override
    public void doUpdate(Connection connection) {
        createTenantTable(connection);
    }

    private void createTenantTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                            CREATE TABLE IF NOT EXISTS tenant (
                                id INTEGER PRIMARY KEY,
                                name TEXT NOT NULL,
                                created_at TEXT NOT NULL,
                                updated_at TEXT NOT NULL
                            )
                            """);
        } catch (Exception e) {
            throw new UpdateException("Error creating tenant table", e);
        }
    }
}

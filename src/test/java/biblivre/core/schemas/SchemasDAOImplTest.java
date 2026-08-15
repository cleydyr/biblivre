package biblivre.core.schemas;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
class SchemasDAOImplTest extends AbstractContainerDatabaseTest {
    @Autowired private SchemaDAO schemaDAO;
    @Autowired private DataSource dataSource;

    @Test
    void deleteRemovesCatalogRowAndDropsPostgresSchema() {
        String schemaName = "delete_probe";
        String schemaTitle = "Delete Probe";

        SchemaThreadLocal.withGlobalSchema(
                () -> {
                    try (Connection connection = dataSource.getConnection();
                            Statement statement = connection.createStatement()) {
                        statement.execute("CREATE SCHEMA \"" + schemaName + "\"");
                        statement.execute(
                                "INSERT INTO schemas (schema, name, disabled) VALUES ('"
                                        + schemaName
                                        + "', '"
                                        + schemaTitle
                                        + "', false)");
                    }

                    SchemaDTO schemaToDelete = new SchemaDTO(schemaName, schemaTitle);

                    assertTrue(schemaDAO.delete(schemaToDelete));
                    assertFalse(schemaDAO.exists(schemaName));

                    try (Connection connection = dataSource.getConnection();
                            PreparedStatement preparedStatement =
                                    connection.prepareStatement(
                                            "SELECT 1 FROM information_schema.schemata"
                                                    + " WHERE schema_name = ?")) {
                        preparedStatement.setString(1, schemaName);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            assertFalse(resultSet.next());
                        }
                    }

                    return null;
                });
    }
}

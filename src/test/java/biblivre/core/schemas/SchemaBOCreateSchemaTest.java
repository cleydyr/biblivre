package biblivre.core.schemas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.administration.setup.State;
import biblivre.core.SchemaThreadLocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
class SchemaBOCreateSchemaTest extends AbstractContainerDatabaseTest {
    private static final String SCHEMA_NAME = "upgrade_probe";

    @Autowired private SchemaBO schemaBO;
    @Autowired private DataSource dataSource;

    @Test
    void createSchemaAppliesSchemaUpgradesMissingFromTheTemplate() throws Exception {
        SchemaDTO schema = new SchemaDTO(SCHEMA_NAME, "Upgrade Probe");
        schema.setCreatedBy(1);

        State.start();
        try {
            assertTrue(schemaBO.createSchema(schema, true, "Probe subtitle"));

            assertTrue(tableExists("record_data"), "v6_0_0$4_0_0$alpha creates record_data");
            assertEquals(
                    0,
                    countBriefFormatsWithLegacyToken(),
                    "v6_0_0$6_0_0$alpha rewrites ${b. } in biblio_brief_formats");
            assertTrue(
                    tableExists("biblio_record_search"),
                    "intelligent-search schema updates create biblio_record_search");
            assertTrue(
                    foreignKeyExists("FK_users_users_types"),
                    "v6_0_0$9_0_8$alpha adds the users.type foreign key");
            assertTrue(
                    versionInstalled("v6_0_0$9_0_8$alpha"),
                    "schema upgrades must be recorded on the new library");
        } finally {
            State.finish();
            SchemaThreadLocal.withGlobalSchema(() -> schemaBO.deleteSchema(schema));
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        String sql =
                """
				SELECT 1
				FROM information_schema.tables
				WHERE table_schema = ? AND table_name = ?
				""";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, SCHEMA_NAME);
            statement.setString(2, tableName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private int countBriefFormatsWithLegacyToken() throws SQLException {
        String sql =
                """
				SELECT count(*)
				FROM upgrade_probe.biblio_brief_formats
				WHERE format LIKE '%${b. }%'
				""";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private boolean foreignKeyExists(String constraintName) throws SQLException {
        String sql =
                """
				SELECT 1
				FROM pg_constraint constraint_row
				JOIN pg_namespace namespace
					ON namespace.oid = constraint_row.connamespace
				WHERE namespace.nspname = ?
				  AND constraint_row.conname = ?
				""";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, SCHEMA_NAME);
            statement.setString(2, constraintName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private boolean versionInstalled(String version) throws SQLException {
        String sql =
                """
				SELECT 1
				FROM upgrade_probe.versions
				WHERE installed_versions = ?
				""";
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, version);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}

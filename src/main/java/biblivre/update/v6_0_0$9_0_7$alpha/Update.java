package biblivre.update.v6_0_0$9_0_7$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.stereotype.Component;

/**
 * Report tables created by Hibernate {@code ddl-auto=update} have {@code id bigint not null} with
 * no sequence default. JDBC inserts omit {@code id} and then fail the not-null constraint. Attach a
 * BIGSERIAL-compatible default on databases that still have the Hibernate shape.
 */
@Component
public class Update implements UpdateService {

    private static final String[] REPORT_TABLES = {"report", "report_parameters", "report_fill"};

    @Override
    public void doUpdate(Connection connection) {
        try {
            for (String table : REPORT_TABLES) {
                ensureIdSerialDefault(connection, "global", table);
            }
        } catch (SQLException e) {
            throw new UpdateException("Error attaching serial defaults to report tables", e);
        }
    }

    static void ensureIdSerialDefault(Connection connection, String schema, String table)
            throws SQLException {
        validateIdentifier(schema);
        validateIdentifier(table);

        if (!tableExists(connection, schema, table)) {
            return;
        }

        if (hasIdDefault(connection, schema, table)) {
            return;
        }

        String qualifiedTable = schema + "." + table;
        String qualifiedSequence = schema + "." + table + "_id_seq";

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SEQUENCE IF NOT EXISTS " + qualifiedSequence);
            statement.execute(
                    "ALTER SEQUENCE " + qualifiedSequence + " OWNED BY " + qualifiedTable + ".id");
            statement.execute(
                    "ALTER TABLE "
                            + qualifiedTable
                            + " ALTER COLUMN id SET DEFAULT nextval('"
                            + qualifiedSequence
                            + "')");
            try (ResultSet ignored =
                    statement.executeQuery(
                            "SELECT setval('"
                                    + qualifiedSequence
                                    + "', COALESCE((SELECT MAX(id) FROM "
                                    + qualifiedTable
                                    + "), 0) + 1, false)")) {
                // setval is invoked by executing the query
            }
        }
    }

    private static boolean tableExists(Connection connection, String schema, String table)
            throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        """
                        SELECT 1
                        FROM information_schema.tables
                        WHERE table_schema = ? AND table_name = ?
                        """)) {
            statement.setString(1, schema);
            statement.setString(2, table);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static boolean hasIdDefault(Connection connection, String schema, String table)
            throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        """
                        SELECT column_default
                        FROM information_schema.columns
                        WHERE table_schema = ? AND table_name = ? AND column_name = 'id'
                        """)) {
            statement.setString(1, schema);
            statement.setString(2, table);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return true;
                }
                String columnDefault = resultSet.getString(1);
                return columnDefault != null && !columnDefault.isBlank();
            }
        }
    }

    private static void validateIdentifier(String identifier) {
        if (identifier == null || !identifier.matches("[a-z][a-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + identifier);
        }
    }
}

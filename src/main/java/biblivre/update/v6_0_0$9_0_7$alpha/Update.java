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
        String sequence = table + "_id_seq";
        String qualifiedSequence = schema + "." + sequence;

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE SEQUENCE IF NOT EXISTS " + qualifiedSequence);
            // OWNED BY requires the sequence and the table to have the same owner.
            // CREATE SEQUENCE leaves the sequence owned by the current role, which may
            // differ from a table created earlier by another role.
            alignSequenceOwnerWithTable(connection, schema, table, sequence);
            statement.execute(
                    "ALTER SEQUENCE " + qualifiedSequence + " OWNED BY " + qualifiedTable + ".id");
            statement.execute(
                    "ALTER TABLE "
                            + qualifiedTable
                            + " ALTER COLUMN id SET DEFAULT nextval('"
                            + qualifiedSequence
                            + "')");
            try (ResultSet _ =
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

    private static void alignSequenceOwnerWithTable(
            Connection connection, String schema, String table, String sequence)
            throws SQLException {
        String alignOwner = ownerAlignmentStatement(connection, schema, table, sequence);
        if (alignOwner == null) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(alignOwner);
        }
    }

    /**
     * Builds {@code ALTER SEQUENCE ... OWNER TO} when the sequence owner differs from the table
     * owner. Identifiers are quoted by PostgreSQL {@code format(%I)}. Returns null when the owners
     * already match, so an update running as the table owner does not need extra schema privileges.
     */
    private static String ownerAlignmentStatement(
            Connection connection, String schema, String table, String sequence)
            throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        """
                        SELECT CASE
                                   WHEN seq.relowner = tab.relowner THEN NULL
                                   ELSE format(
                                       'ALTER SEQUENCE %I.%I OWNER TO %I',
                                       seq_ns.nspname,
                                       seq.relname,
                                       pg_get_userbyid(tab.relowner))
                               END
                        FROM pg_class seq
                        JOIN pg_namespace seq_ns ON seq_ns.oid = seq.relnamespace
                        JOIN pg_class tab ON tab.relname = ? AND tab.relkind = 'r'
                        JOIN pg_namespace tab_ns
                            ON tab_ns.oid = tab.relnamespace AND tab_ns.nspname = ?
                        WHERE seq_ns.nspname = ?
                          AND seq.relname = ?
                          AND seq.relkind = 'S'
                        """)) {
            statement.setString(1, table);
            statement.setString(2, schema);
            statement.setString(3, schema);
            statement.setString(4, sequence);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return resultSet.getString(1);
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

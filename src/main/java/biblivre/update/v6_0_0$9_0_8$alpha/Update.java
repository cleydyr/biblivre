package biblivre.update.v6_0_0$9_0_8$alpha;

import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.stereotype.Component;

/**
 * Readers can keep a {@code users.type} whose row in {@code users_types} was removed, because that
 * column had no foreign key and type deletion ignored inactive readers. Insert a placeholder for
 * each missing id so lending can still read a fine, then reject new orphans.
 */
@Component
public class Update implements UpdateService {

    static final String PLACEHOLDER_DESCRIPTION =
            "Criado automaticamente porque ainda há leitores associados a este tipo.";

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        restoreMissingUserTypes(connection);
    }

    static void restoreMissingUserTypes(Connection connection) {
        try {
            insertPlaceholders(connection);
            advanceUserTypeSequence(connection);
            addUserTypeForeignKey(connection);
        } catch (SQLException exception) {
            throw new UpdateException("Error restoring missing user types", exception);
        }
    }

    static String placeholderName(int typeId) {
        return "Tipo de usuário ausente (#" + typeId + ")";
    }

    private static void insertPlaceholders(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                    INSERT INTO users_types (
                        id, name, description, lending_limit, reservation_limit,
                        lending_time_limit, reservation_time_limit, fine_value, created_by)
                    SELECT DISTINCT u.type,
                           'Tipo de usuário ausente (#' || u.type || ')',
                           'Criado automaticamente porque ainda há leitores associados a este tipo.',
                           0, 0, 0, 0, 0, 1
                    FROM users u
                    WHERE u.type IS NOT NULL
                      AND NOT EXISTS (SELECT 1 FROM users_types t WHERE t.id = u.type)
                    """);
        }
    }

    private static void advanceUserTypeSequence(Connection connection) throws SQLException {
        String sequenceName = serialSequenceName(connection);
        if (sequenceName == null) {
            return;
        }

        long highestTypeId = highestUserTypeId(connection);
        SequencePosition position = sequencePosition(connection, sequenceName);
        // setval(n) makes the following nextval return n + 1. Advance only when an explicit id is
        // already at or beyond the value the sequence would hand out next.
        long nextValue = position.called() ? position.lastValue() + 1 : position.lastValue();
        if (highestTypeId < nextValue) {
            return;
        }

        try (PreparedStatement statement =
                connection.prepareStatement("SELECT setval(?::regclass, ?)")) {
            statement.setString(1, sequenceName);
            statement.setLong(2, highestTypeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
            }
        }
    }

    private static String serialSequenceName(Connection connection) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement("SELECT pg_get_serial_sequence('users_types', 'id')")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return resultSet.getString(1);
            }
        }
    }

    private static long highestUserTypeId(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet resultSet =
                        statement.executeQuery("SELECT COALESCE(MAX(id), 0) FROM users_types")) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    private static SequencePosition sequencePosition(Connection connection, String sequenceName)
            throws SQLException {
        if (!sequenceName.matches("[a-z_][a-z0-9_]*(\\.[a-z_][a-z0-9_]*)?")) {
            throw new SQLException("Unexpected sequence name: " + sequenceName);
        }

        try (Statement statement = connection.createStatement();
                ResultSet resultSet =
                        statement.executeQuery(
                                "SELECT last_value, is_called FROM " + sequenceName)) {
            if (!resultSet.next()) {
                return new SequencePosition(0, true);
            }
            return new SequencePosition(
                    resultSet.getLong("last_value"), resultSet.getBoolean("is_called"));
        }
    }

    private record SequencePosition(long lastValue, boolean called) {}

    private static void addUserTypeForeignKey(Connection connection) throws SQLException {
        if (foreignKeyExists(connection)) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                    ALTER TABLE users
                    ADD CONSTRAINT "FK_users_users_types"
                    FOREIGN KEY (type) REFERENCES users_types (id)
                    """);
        }
    }

    private static boolean foreignKeyExists(Connection connection) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        """
                        SELECT 1
                        FROM pg_constraint constraint_row
                        JOIN pg_namespace namespace
                            ON namespace.oid = constraint_row.connamespace
                        WHERE namespace.nspname = current_schema()
                          AND constraint_row.conname = 'FK_users_users_types'
                        """)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}

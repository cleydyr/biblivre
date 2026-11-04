package biblivre.update.v6_0_0$9_0_8$alpha;

import biblivre.core.utils.Constants;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Readers can keep a {@code users.type} whose row in {@code users_types} was removed, because that
 * column had no foreign key and type deletion ignored inactive readers. Insert a placeholder for
 * each missing id so lending can still read a fine, then reject new orphans.
 */
@Component
public class Update implements UpdateService {

    record PlaceholderText(String namePrefix, String description) {}

    static final String DEFAULT_LANGUAGE = "pt-BR";

    static final Map<String, PlaceholderText> PLACEHOLDER_TEXTS =
            Map.of(
                    "pt-BR",
                    new PlaceholderText(
                            "Tipo de usuário ausente",
                            "Criado automaticamente porque ainda há leitores associados a este"
                                    + " tipo."),
                    "en-US",
                    new PlaceholderText(
                            "Missing user type",
                            "Created automatically because readers are still assigned to this"
                                    + " type."),
                    "es",
                    new PlaceholderText(
                            "Tipo de usuario ausente",
                            "Creado automáticamente porque todavía hay lectores asociados a este"
                                    + " tipo."));

    // Same limits lending and reservation already fall back to when a reader's type is missing.
    static final int PLACEHOLDER_LENDING_LIMIT = 1;
    static final int PLACEHOLDER_RESERVATION_LIMIT = 1;
    static final int PLACEHOLDER_LENDING_TIME_LIMIT = 7;
    static final int PLACEHOLDER_RESERVATION_TIME_LIMIT = 7;

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        restoreMissingUserTypes(connection);
    }

    static void restoreMissingUserTypes(Connection connection) {
        try {
            insertPlaceholders(connection, placeholderText(connection));
            advanceUserTypeSequence(connection);
            addUserTypeForeignKey(connection);
            addUserTypeIndex(connection);
        } catch (SQLException exception) {
            throw new UpdateException("Error restoring missing user types", exception);
        }
    }

    private static PlaceholderText placeholderText(Connection connection) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement("SELECT value FROM configurations WHERE key = ?")) {
            statement.setString(1, Constants.CONFIG_DEFAULT_LANGUAGE);
            try (ResultSet resultSet = statement.executeQuery()) {
                String language = resultSet.next() ? resultSet.getString(1) : DEFAULT_LANGUAGE;
                return PLACEHOLDER_TEXTS.getOrDefault(
                        language, PLACEHOLDER_TEXTS.get(DEFAULT_LANGUAGE));
            }
        }
    }

    private static void insertPlaceholders(Connection connection, PlaceholderText text)
            throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        """
                        INSERT INTO users_types (
                            id, name, description, lending_limit, reservation_limit,
                            lending_time_limit, reservation_time_limit, fine_value, created_by)
                        SELECT DISTINCT u.type, ?::text || ' (#' || u.type || ')', ?, ?, ?, ?, ?, 0, 1
                        FROM users u
                        WHERE u.type IS NOT NULL
                          AND NOT EXISTS (SELECT 1 FROM users_types t WHERE t.id = u.type)
                        """)) {
            statement.setString(1, text.namePrefix());
            statement.setString(2, text.description());
            statement.setInt(3, PLACEHOLDER_LENDING_LIMIT);
            statement.setInt(4, PLACEHOLDER_RESERVATION_LIMIT);
            statement.setInt(5, PLACEHOLDER_LENDING_TIME_LIMIT);
            statement.setInt(6, PLACEHOLDER_RESERVATION_TIME_LIMIT);
            statement.executeUpdate();
        }
    }

    private static void advanceUserTypeSequence(Connection connection) throws SQLException {
        // Placeholders keep their original ids, which may be at or past the sequence. Move the
        // sequence past the highest id. nextval may burn one value when the sequence is already
        // ahead, which is harmless. The regclass cast handles quoted schema names.
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                    SELECT setval(
                        sequence_name::regclass,
                        GREATEST(
                            nextval(sequence_name::regclass),
                            (SELECT MAX(id) FROM users_types)))
                    FROM (SELECT pg_get_serial_sequence('users_types', 'id') AS sequence_name) serial
                    WHERE sequence_name IS NOT NULL
                    """);
        }
    }

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

    // Serves the type-in-use check on deletion and the foreign key check on users_types deletes.
    private static void addUserTypeIndex(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(
                    """
                    CREATE INDEX IF NOT EXISTS "FKI_users_users_types" ON users USING btree (type)
                    """);
        }
    }
}

package biblivre.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds SQL fragments together with their bind parameters so placeholder order cannot drift from
 * the values passed to {@link PreparedStatement}.
 */
public final class SqlParts {
    private final StringBuilder sql = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();

    public SqlParts append(String fragment, Object... values) {
        sql.append(fragment);
        for (Object value : values) {
            parameters.add(value);
        }
        return this;
    }

    public String sql() {
        return sql.toString();
    }

    public Object[] parameters() {
        return parameters.toArray();
    }

    public void assertPlaceholderCount() {
        long placeholderCount = sql.chars().filter(character -> character == '?').count();
        if (placeholderCount != parameters.size()) {
            throw new IllegalStateException(
                    "SQL has %d placeholders but %d parameters"
                            .formatted(placeholderCount, parameters.size()));
        }
    }

    public PreparedStatement prepare(Connection connection) throws SQLException {
        assertPlaceholderCount();
        PreparedStatement preparedStatement = connection.prepareStatement(sql());
        PreparedStatementUtil.setAllParameters(preparedStatement, parameters());
        return preparedStatement;
    }
}

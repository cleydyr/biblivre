package biblivre.update.v6_0_0$8_0_1$alpha;

import biblivre.cataloging.holding.HoldingDTO;
import biblivre.core.utils.Constants;
import biblivre.record.RecordDataJDBCDAO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {
    private static final Logger log = LoggerFactory.getLogger(Update.class);

    private static final int BATCH_SIZE = 500;

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        fixHoldingControlNumbers(connection);
    }

    private void fixHoldingControlNumbers(Connection connection) {
        int fixed = 0;

        try (PreparedStatement select =
                        connection.prepareStatement(
                                """
                                SELECT id, iso2709
                                FROM biblio_holdings
                                ORDER BY id
                                OFFSET ? LIMIT ?
                                """);
                PreparedStatement update =
                        connection.prepareStatement(
                                """
                                UPDATE biblio_holdings
                                SET iso2709 = ?, modified = now()
                                WHERE id = ?
                                """)) {
            for (int offset = 0; ; offset += BATCH_SIZE) {
                select.setInt(1, offset);
                select.setInt(2, BATCH_SIZE);

                int rows = 0;

                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        rows++;

                        if (fixHoldingIfNeeded(rs, update, connection)) {
                            fixed++;
                        }
                    }
                }

                if (rows < BATCH_SIZE) {
                    break;
                }
            }
        } catch (SQLException e) {
            throw new UpdateException("Error fixing holding MARC 001 control numbers", e);
        }

        log.info("Fixed {} holding MARC 001 control numbers", fixed);
    }

    private boolean fixHoldingIfNeeded(
            ResultSet rs, PreparedStatement update, Connection connection) throws SQLException {
        int id = rs.getInt("id");
        String iso2709 = rs.getString("iso2709");

        HoldingDTO holding = new HoldingDTO();
        holding.setIso2709(iso2709.getBytes(Constants.DEFAULT_CHARSET));

        Record record = holding.getRecord();

        if (record == null) {
            log.warn("Skipping holding {} with unreadable MARC data", id);
            return false;
        }

        if (!needsControlNumberFix(record, id)) {
            return false;
        }

        holding.setId(id);

        update.setString(1, holding.getUTF8Iso2709());
        update.setInt(2, id);
        update.executeUpdate();

        try {
            RecordDataJDBCDAO.updateRecordData(connection, holding);
        } catch (SQLException e) {
            throw new UpdateException("Error updating record_data for holding %d".formatted(id), e);
        }

        return true;
    }

    static boolean needsControlNumberFix(Record record, int id) {
        var fields001 = record.getVariableFields("001");

        if (fields001.size() != 1) {
            return true;
        }

        return !expectedControlNumber(id).equals(record.getControlNumber());
    }

    static String expectedControlNumber(int id) {
        return String.format("%07d", id);
    }
}

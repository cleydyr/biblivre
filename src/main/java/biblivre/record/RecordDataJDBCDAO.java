package biblivre.record;

import biblivre.cataloging.RecordDTO;
import biblivre.core.PreparedStatementUtil;
import java.sql.Connection;
import java.sql.SQLException;
import org.marc4j.marc.Record;

public class RecordDataJDBCDAO {
    public static void insertRecordData(Connection connection, RecordDTO record)
            throws SQLException {
        var sql =
                """
                INSERT INTO record_data (record_id, field, subfield, value, record_type)
                VALUES (?, ?, ?, ?, ?)
                """;

        Record marcRecord = record.getRecord();

        try (var statement = connection.prepareStatement(sql)) {
            for (var dataField : marcRecord.getDataFields()) {
                for (var subfield : dataField.getSubfields()) {
                    PreparedStatementUtil.setAllParameters(
                            statement,
                            record.getId(),
                            dataField.getTag(),
                            String.valueOf(subfield.getCode()),
                            subfield.getData(),
                            record.getRecordType().toString());

                    statement.addBatch();
                }
            }

            for (var controlField : marcRecord.getControlFields()) {
                statement.setLong(1, record.getId());
                statement.setString(2, controlField.getTag());
                statement.setString(3, null);
                statement.setString(4, controlField.getData());
                statement.setString(5, record.getRecordType().toString());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    public static void updateRecordData(Connection connection, RecordDTO record)
            throws SQLException {
        deleteRecordData(connection, record);

        insertRecordData(connection, record);
    }

    public static void deleteRecordData(Connection connection, RecordDTO record)
            throws SQLException {
        var sql =
                """
                DELETE FROM record_data WHERE record_id = ? AND record_type = ?
                """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, record.getId());
            statement.setString(2, record.getRecordType().toString());

            statement.executeUpdate();
        }
    }
}

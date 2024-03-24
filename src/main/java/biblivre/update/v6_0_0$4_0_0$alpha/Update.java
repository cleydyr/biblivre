package biblivre.update.v6_0_0$4_0_0$alpha;

import biblivre.cataloging.RecordDAO;
import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.holding.HoldingDAO;
import biblivre.record.RecordDataJDBCDAO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    @Autowired
    @Qualifier("recordDAO")
    private RecordDAO recordDAO;

    @Autowired
    @Qualifier("holdingDAO")
    private HoldingDAO holdingDAO;

    public void doUpdateScopedBySchema(Connection connection) throws UpdateException {
        _createRecordDataTable(connection);
        _createRecordDataIndices(connection);
        _migrateRecordData(connection);
    }

    private void _createRecordDataTable(Connection connection) throws UpdateException {
        String sql =
                """
                        CREATE TABLE IF NOT EXISTS record_data (
                            id SERIAL,
                            record_id BIGINT NOT NULL,
                            record_type VARCHAR(20) NOT NULL,
                            field VARCHAR(3) NOT NULL,
                            subfield VARCHAR(1),
                            value TEXT NOT NULL
                        )
                        """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new UpdateException("failed to create table", e);
        }
    }

    private void _createRecordDataIndices(Connection connection) throws UpdateException {
        String sql =
                """
                        CREATE INDEX IF NOT EXISTS record_data_record_id_idx ON record_data (record_id);
                        CREATE INDEX IF NOT EXISTS record_data_field_idx ON record_data (field, subfield);
                        CREATE INDEX IF NOT EXISTS record_data_record_type_idx ON record_data (record_type);
                        """;
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (Exception e) {
            throw new UpdateException("failed to create table", e);
        }
    }

    private void _migrateRecordData(Connection connection) throws UpdateException {
        _migrateHoldingRecords(connection);
        _migrateOtherRecords(connection);
    }

    private void _migrateHoldingRecords(Connection connection) {
        int limit = 500;

        for (int offset = 0; ; offset += limit) {
            List<RecordDTO> records = holdingDAO.list(offset, limit);

            if (records.isEmpty()) {
                break;
            }

            _migrateRecords(connection, records);
        }
    }

    private void _migrateOtherRecords(Connection connection) {
        for (var recordType : RecordType.values()) {
            if (recordType == RecordType.HOLDING) {
                continue;
            }

            int limit = 500;

            for (int offset = 0; ; offset += limit) {
                List<RecordDTO> records = recordDAO.list(offset, limit, recordType);

                if (records.isEmpty()) {
                    break;
                }

                _migrateRecords(connection, records);
            }
        }
    }

    private void _migrateRecords(Connection connection, List<RecordDTO> records) {
        for (var record : records) {
            _migrateRecord(connection, record);
        }
    }

    private void _migrateRecord(Connection connection, RecordDTO record) {
        try {
            RecordDataJDBCDAO.insertRecordData(connection, record);
        } catch (Exception e) {
            throw new UpdateException("failed to migrate record " + record.getId(), e);
        }
    }
}

package biblivre.update.v6_0_0$9_0_9$alpha;

import biblivre.cataloging.RecordDTO;
import biblivre.cataloging.enums.RecordType;
import biblivre.marc.Iso2709;
import biblivre.record.RecordDataJDBCDAO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.marc4j.MarcException;
import org.springframework.stereotype.Component;

/**
 * marc4j wrote fields longer than 9999 bytes with a directory length of 9999, so those rows can't
 * be read back and break any listing that includes them, like the search Excel export. Rebuild each
 * one from its directory offsets and store it again with the long fields split.
 */
@Component
@Slf4j
public class Update implements UpdateService {
    private static final int BATCH_SIZE = 500;

    private static final Map<String, RecordType> TABLES =
            Map.of(
                    "biblio_records", RecordType.BIBLIO,
                    "authorities_records", RecordType.AUTHORITIES,
                    "vocabulary_records", RecordType.VOCABULARY,
                    "biblio_holdings", RecordType.HOLDING);

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        TABLES.forEach((table, recordType) -> repairTable(connection, table, recordType));
    }

    private static void repairTable(Connection connection, String table, RecordType recordType) {
        int repaired = 0;

        // Only a record of at least 10000 bytes can hold a field the directory could not measure
        try (PreparedStatement select =
                        connection.prepareStatement(
                                """
                                SELECT id, iso2709
                                FROM %s
                                WHERE id > ? AND octet_length(iso2709) > ?
                                ORDER BY id
                                LIMIT ?
                                """
                                        .formatted(table));
                PreparedStatement update =
                        connection.prepareStatement(
                                "UPDATE %s SET iso2709 = ? WHERE id = ?".formatted(table))) {
            int lastId = 0;

            for (; ; ) {
                select.setInt(1, lastId);
                select.setInt(2, Iso2709.MAX_FIELD_LENGTH);
                select.setInt(3, BATCH_SIZE);

                int rows = 0;

                try (ResultSet rs = select.executeQuery()) {
                    while (rs.next()) {
                        rows++;
                        lastId = rs.getInt("id");

                        RecordDTO dto = repair(rs.getBytes("iso2709"), recordType, lastId);

                        if (dto == null) {
                            continue;
                        }

                        update.setString(1, dto.getUTF8Iso2709());
                        update.setInt(2, lastId);
                        update.executeUpdate();

                        RecordDataJDBCDAO.updateRecordData(connection, dto);

                        repaired++;
                    }
                }

                if (rows < BATCH_SIZE) {
                    break;
                }
            }
        } catch (SQLException e) {
            throw new UpdateException("Error repairing oversized MARC fields in " + table, e);
        }

        log.info("Repaired {} {} records with oversized MARC fields", repaired, recordType);
    }

    /**
     * @return the record rebuilt with its long fields split, or null when the stored bytes are fine
     *     or can't be rebuilt
     */
    static RecordDTO repair(byte[] iso2709, RecordType recordType, int id) {
        try {
            if (!Iso2709.needsLenientParse(iso2709)) {
                return null;
            }

            RecordDTO dto = recordType.getRecordClass().getConstructor().newInstance();

            dto.setRecord(Iso2709.parseLenient(iso2709));
            dto.setId(id);

            if (Iso2709.needsLenientParse(dto.getIso2709())) {
                log.warn("Could not repair {} record {}: still malformed", recordType, id);

                return null;
            }

            return dto;
        } catch (MarcException e) {
            log.warn("Could not repair {} record {}", recordType, id, e);

            return null;
        } catch (ReflectiveOperationException e) {
            throw new UpdateException("Cannot create " + recordType.getRecordClass(), e);
        }
    }
}

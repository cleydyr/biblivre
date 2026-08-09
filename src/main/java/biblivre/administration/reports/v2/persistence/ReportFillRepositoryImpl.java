package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.model.ReportFill;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class ReportFillRepositoryImpl extends AbstractDAO implements ReportFillRepository {

    @Override
    public ReportFill save(ReportFill reportFill) {
        return withTransactionContext(
                connection -> {
                    insertFill(connection, reportFill);
                    insertFillParameters(connection, reportFill);
                    return reportFill;
                });
    }

    private static void insertFill(Connection connection, ReportFill reportFill) throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement(
                        """
                        INSERT INTO global.report_fill (report_id, digital_media_id)
                        VALUES (?, ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS)) {
            pst.setLong(1, reportFill.getReport().getId());
            pst.setInt(2, reportFill.getDigitalMediaId());
            pst.executeUpdate();

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DAOException(new Exception("Missing generated report fill id"));
                }
                reportFill.setId(keys.getLong(1));
            }
        }
    }

    private static void insertFillParameters(Connection connection, ReportFill reportFill)
            throws Exception {
        Map<String, String> parameters = reportFill.getFillParameters();
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        try (PreparedStatement pst =
                connection.prepareStatement(
                        """
                        INSERT INTO global.report_fill_parameters
                            (report_fill_id, parameter_name, parameter_value)
                        VALUES (?, ?, ?)
                        """)) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                pst.setLong(1, reportFill.getId());
                pst.setString(2, entry.getKey());
                pst.setString(3, entry.getValue());
                pst.addBatch();
            }
            pst.executeBatch();
        }
    }
}

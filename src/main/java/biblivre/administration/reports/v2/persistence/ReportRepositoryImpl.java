package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.model.Report;
import biblivre.administration.reports.v2.model.ReportParameter;
import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepositoryImpl extends AbstractDAO implements ReportRepository {

    @Override
    public Report save(Report report) {
        return withTransactionContext(
                connection -> {
                    if (report.getId() == 0) {
                        return insert(connection, report);
                    }
                    return update(connection, report);
                });
    }

    private Report insert(Connection connection, Report report) throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement(
                        """
                        INSERT INTO global.report (name, description, schema, digital_media_id)
                        VALUES (?, ?, ?, ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, report.getName());
            pst.setString(2, report.getDescription());
            pst.setString(3, report.getSchema());
            pst.setLong(4, report.getDigitalMediaId());
            pst.executeUpdate();

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DAOException(new Exception("Missing generated report id"));
                }
                report.setId(keys.getLong(1));
            }
        }

        if (report.getParameters() != null) {
            for (ReportParameter parameter : report.getParameters()) {
                parameter.setReport(report);
                insertParameter(connection, parameter);
            }
        }

        return report;
    }

    private Report update(Connection connection, Report report) throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement(
                        """
                        UPDATE global.report
                        SET name = ?, description = ?, schema = ?, digital_media_id = ?
                        WHERE id = ?
                        """)) {
            pst.setString(1, report.getName());
            pst.setString(2, report.getDescription());
            pst.setString(3, report.getSchema());
            pst.setLong(4, report.getDigitalMediaId());
            pst.setLong(5, report.getId());
            pst.executeUpdate();
        }
        return report;
    }

    private void insertParameter(Connection connection, ReportParameter parameter)
            throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement(
                        """
                        INSERT INTO global.report_parameters (name, type, description, report_id)
                        VALUES (?, ?, ?, ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, parameter.getName());
            pst.setString(2, parameter.getType());
            pst.setString(3, parameter.getDescription());
            pst.setLong(4, parameter.getReport().getId());
            pst.executeUpdate();

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DAOException(new Exception("Missing generated report parameter id"));
                }
                parameter.setId(keys.getLong(1));
            }
        }
    }

    @Override
    public List<Report> findAllWithParameters() {
        try (Connection connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement(
                                """
                                SELECT r.id, r.name, r.description, r.schema, r.digital_media_id,
                                       p.id AS parameter_id, p.name AS parameter_name,
                                       p.type AS parameter_type, p.description AS parameter_description
                                FROM global.report r
                                LEFT JOIN global.report_parameters p ON p.report_id = r.id
                                ORDER BY r.id, p.id
                                """);
                ResultSet rs = pst.executeQuery()) {
            return mapReportsWithParameters(rs);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<Report> findByIdWithParameters(Long id) {
        try (Connection connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement(
                                """
                                SELECT r.id, r.name, r.description, r.schema, r.digital_media_id,
                                       p.id AS parameter_id, p.name AS parameter_name,
                                       p.type AS parameter_type, p.description AS parameter_description
                                FROM global.report r
                                LEFT JOIN global.report_parameters p ON p.report_id = r.id
                                WHERE r.id = ?
                                ORDER BY p.id
                                """)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                List<Report> reports = mapReportsWithParameters(rs);
                return reports.stream().findFirst();
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public Optional<Report> findById(Long id) {
        try (Connection connection = datasource.getConnection();
                PreparedStatement pst =
                        connection.prepareStatement(
                                """
                                SELECT id, name, description, schema, digital_media_id
                                FROM global.report
                                WHERE id = ?
                                """)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapReport(rs, Collections.emptyList()));
            }
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        withTransactionContext(
                connection -> {
                    deleteFillParametersByReportId(connection, id);
                    deleteFillsByReportId(connection, id);
                    deleteParametersByReportId(connection, id);
                    try (PreparedStatement pst =
                            connection.prepareStatement("DELETE FROM global.report WHERE id = ?")) {
                        pst.setLong(1, id);
                        pst.executeUpdate();
                    }
                });
    }

    @Override
    public void deleteAll() {
        withTransactionContext(
                connection -> {
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate("DELETE FROM global.report_fill_parameters");
                        statement.executeUpdate("DELETE FROM global.report_fill");
                        statement.executeUpdate("DELETE FROM global.report_parameters");
                        statement.executeUpdate("DELETE FROM global.report");
                    }
                });
    }

    private static void deleteFillParametersByReportId(Connection connection, long reportId)
            throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement(
                        """
                        DELETE FROM global.report_fill_parameters
                        WHERE report_fill_id IN (
                            SELECT id FROM global.report_fill WHERE report_id = ?
                        )
                        """)) {
            pst.setLong(1, reportId);
            pst.executeUpdate();
        }
    }

    private static void deleteFillsByReportId(Connection connection, long reportId)
            throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement("DELETE FROM global.report_fill WHERE report_id = ?")) {
            pst.setLong(1, reportId);
            pst.executeUpdate();
        }
    }

    private static void deleteParametersByReportId(Connection connection, long reportId)
            throws Exception {
        try (PreparedStatement pst =
                connection.prepareStatement(
                        "DELETE FROM global.report_parameters WHERE report_id = ?")) {
            pst.setLong(1, reportId);
            pst.executeUpdate();
        }
    }

    private static List<Report> mapReportsWithParameters(ResultSet rs) throws Exception {
        Map<Long, Report> reports = new LinkedHashMap<>();

        while (rs.next()) {
            long reportId = rs.getLong("id");
            Report report =
                    reports.computeIfAbsent(
                            reportId,
                            id -> {
                                try {
                                    return mapReport(rs, new ArrayList<>());
                                } catch (Exception e) {
                                    throw new DAOException(e);
                                }
                            });

            long parameterId = rs.getLong("parameter_id");
            if (!rs.wasNull()) {
                ReportParameter parameter =
                        new ReportParameter(
                                parameterId,
                                rs.getString("parameter_name"),
                                rs.getString("parameter_type"),
                                rs.getString("parameter_description"),
                                report);
                report.getParameters().add(parameter);
            }
        }

        return new ArrayList<>(reports.values());
    }

    private static Report mapReport(ResultSet rs, List<ReportParameter> parameters)
            throws Exception {
        return new Report(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                parameters,
                Collections.emptyList(),
                rs.getString("schema"),
                rs.getLong("digital_media_id"));
    }
}

package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.model.Report;
import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    Report save(Report report);

    List<Report> findAllWithParameters();

    Optional<Report> findByIdWithParameters(Long id);

    Optional<Report> findById(Long id);

    void deleteById(Long id);

    void deleteAll();
}

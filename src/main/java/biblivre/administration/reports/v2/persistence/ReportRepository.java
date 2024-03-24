package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.model.Report;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends CrudRepository<Report, Long> {}

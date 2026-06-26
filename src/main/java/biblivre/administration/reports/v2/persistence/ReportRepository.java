package biblivre.administration.reports.v2.persistence;

import biblivre.administration.reports.v2.model.Report;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends CrudRepository<Report, Long> {
    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.parameters")
    List<Report> findAllWithParameters();

    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.parameters WHERE r.id = :id")
    Optional<Report> findByIdWithParameters(@Param("id") Long id);
}

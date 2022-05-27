package biblivre.legacy.repository;

import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.administration.accesscards.AccessCardStatus;
import biblivre.legacy.entity.AccessCard;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessCardRepository extends JpaRepository<AccessCard, Integer> {

    AccessCardDTO getByCode(String code);

    Page<AccessCard> findByCodeContaining(String code, Pageable pageable);

    @Query(
            "SELECT accessCard FROM AccessCard accessCard WHERE code in (:codes) AND rawAccessCardStatus in (:statuses)")
    Collection<AccessCard> findByCodesInAndStatusIn(
            @Param("codes") Collection<String> codes,
            @Param("statuses") Collection<AccessCardStatus> statuses);
}

package biblivre.legacy.repository;

import biblivre.administration.accesscards.AccessCardStatus;
import biblivre.legacy.entity.AccessCard;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessCardRepository extends JpaRepository<AccessCard, Integer> {

    Optional<AccessCard> getByCode(String code);

    Page<AccessCard> findByCodeContaining(String code, Pageable pageable);

    @Query(
            "SELECT accessCard FROM AccessCard accessCard WHERE code in (:codes) AND rawAccessCardStatus in (:statuses)")
    Collection<AccessCard> findByCodesInAndStatusIn(
            @Param("codes") Collection<String> codes,
            @Param("statuses") Collection<AccessCardStatus> statuses);

    default Page<AccessCard> findByAccessCardStatus(
            AccessCardStatus accessCardStatus, Pageable pageable) {
        return findByRawAccessCardStatus(accessCardStatus.name().toLowerCase(), pageable);
    }

    Page<AccessCard> findByRawAccessCardStatus(String rawAccessCardStatus, Pageable pageable);

    default Page<AccessCard> findByCodeContainingAndAccessCardStatus(
            String code, AccessCardStatus status, Pageable pageable) {
        return findByCodeContainingAndRawAccessCardStatus(
                code, status.name().toLowerCase(), pageable);
    }

    Page<AccessCard> findByCodeContainingAndRawAccessCardStatus(
            String code, String rawAccessCardStatus, Pageable pageable);
}

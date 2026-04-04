package com.gindevp.app.repository;

import com.gindevp.app.domain.ReturnRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnRequest entity.
 */
@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long>, JpaSpecificationExecutor<ReturnRequest> {
    default Optional<ReturnRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReturnRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReturnRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value =
            "select returnRequest from ReturnRequest returnRequest " +
            "left join fetch returnRequest.requester req " +
            "left join fetch req.department",
        countQuery = "select count(returnRequest) from ReturnRequest returnRequest"
    )
    Page<ReturnRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select returnRequest from ReturnRequest returnRequest " +
            "left join fetch returnRequest.requester req " +
            "left join fetch req.department"
    )
    List<ReturnRequest> findAllWithToOneRelationships();

    @Query(
        "select returnRequest from ReturnRequest returnRequest " +
            "left join fetch returnRequest.requester req " +
            "left join fetch req.department where returnRequest.id =:id"
    )
    Optional<ReturnRequest> findOneWithToOneRelationships(@Param("id") Long id);
}

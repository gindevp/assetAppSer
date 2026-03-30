package com.gindevp.app.repository;

import com.gindevp.app.domain.RepairRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RepairRequest entity.
 */
@Repository
public interface RepairRequestRepository extends JpaRepository<RepairRequest, Long>, JpaSpecificationExecutor<RepairRequest> {
    default Optional<RepairRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<RepairRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<RepairRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select repairRequest from RepairRequest repairRequest left join fetch repairRequest.requester left join fetch repairRequest.equipment",
        countQuery = "select count(repairRequest) from RepairRequest repairRequest"
    )
    Page<RepairRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select repairRequest from RepairRequest repairRequest left join fetch repairRequest.requester left join fetch repairRequest.equipment"
    )
    List<RepairRequest> findAllWithToOneRelationships();

    @Query(
        "select repairRequest from RepairRequest repairRequest left join fetch repairRequest.requester left join fetch repairRequest.equipment where repairRequest.id =:id"
    )
    Optional<RepairRequest> findOneWithToOneRelationships(@Param("id") Long id);
}

package com.gindevp.app.repository;

import com.gindevp.app.domain.AllocationRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AllocationRequest entity.
 */
@Repository
public interface AllocationRequestRepository extends JpaRepository<AllocationRequest, Long>, JpaSpecificationExecutor<AllocationRequest> {
    default Optional<AllocationRequest> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AllocationRequest> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AllocationRequest> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value =
            "select distinct allocationRequest from AllocationRequest allocationRequest " +
            "left join fetch allocationRequest.requester req " +
            "left join fetch req.department " +
            "left join fetch allocationRequest.beneficiaryEmployee " +
            "left join fetch allocationRequest.beneficiaryDepartment " +
            "left join fetch allocationRequest.beneficiaryLocation " +
            "left join fetch allocationRequest.stockIssue",
        countQuery = "select count(allocationRequest) from AllocationRequest allocationRequest"
    )
    Page<AllocationRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct allocationRequest from AllocationRequest allocationRequest " +
            "left join fetch allocationRequest.requester req " +
            "left join fetch req.department " +
            "left join fetch allocationRequest.beneficiaryEmployee " +
            "left join fetch allocationRequest.beneficiaryDepartment " +
            "left join fetch allocationRequest.beneficiaryLocation " +
            "left join fetch allocationRequest.stockIssue"
    )
    List<AllocationRequest> findAllWithToOneRelationships();

    @Query(
        "select distinct allocationRequest from AllocationRequest allocationRequest " +
            "left join fetch allocationRequest.requester req " +
            "left join fetch req.department " +
            "left join fetch allocationRequest.beneficiaryEmployee " +
            "left join fetch allocationRequest.beneficiaryDepartment " +
            "left join fetch allocationRequest.beneficiaryLocation " +
            "left join fetch allocationRequest.stockIssue " +
            "where allocationRequest.id = :id"
    )
    Optional<AllocationRequest> findOneWithToOneRelationships(@Param("id") Long id);
}

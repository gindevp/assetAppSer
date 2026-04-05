package com.gindevp.app.repository;

import com.gindevp.app.domain.LossReportRequest;
import com.gindevp.app.domain.enumeration.LossReportRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LossReportRequestRepository extends JpaRepository<LossReportRequest, Long> {
    boolean existsByEquipment_IdAndStatus(Long equipmentId, LossReportRequestStatus status);

    boolean existsByConsumableAssignment_IdAndStatus(Long consumableAssignmentId, LossReportRequestStatus status);

    Page<LossReportRequest> findByRequester_Id(Long requesterId, Pageable pageable);

    @Query(
        "select lr from LossReportRequest lr " +
            "left join fetch lr.requester req " +
            "left join fetch req.department " +
            "left join fetch lr.equipment eq " +
            "left join fetch eq.assetItem " +
            "left join fetch lr.consumableAssignment ca " +
            "left join fetch ca.assetItem " +
            "where lr.id = :id"
    )
    java.util.Optional<LossReportRequest> findOneWithRelationships(@Param("id") Long id);
}

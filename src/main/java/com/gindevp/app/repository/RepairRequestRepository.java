package com.gindevp.app.repository;

import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import java.util.Collection;
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
        value =
            "select distinct repairRequest from RepairRequest repairRequest " +
            "left join fetch repairRequest.requester req " +
            "left join fetch req.department " +
            "left join fetch repairRequest.equipment " +
            "left join fetch repairRequest.lines lines " +
            "left join fetch lines.equipment " +
            "left join fetch lines.assetItem",
        countQuery = "select count(repairRequest) from RepairRequest repairRequest"
    )
    Page<RepairRequest> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct repairRequest from RepairRequest repairRequest " +
            "left join fetch repairRequest.requester req " +
            "left join fetch req.department " +
            "left join fetch repairRequest.equipment " +
            "left join fetch repairRequest.lines lines " +
            "left join fetch lines.equipment " +
            "left join fetch lines.assetItem"
    )
    List<RepairRequest> findAllWithToOneRelationships();

    @Query(
        "select distinct repairRequest from RepairRequest repairRequest " +
            "left join fetch repairRequest.requester req " +
            "left join fetch req.department " +
            "left join fetch repairRequest.equipment " +
            "left join fetch repairRequest.lines lines " +
            "left join fetch lines.equipment " +
            "left join fetch lines.assetItem where repairRequest.id =:id"
    )
    Optional<RepairRequest> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(
        "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RepairRequest r WHERE r.status IN :statuses " +
        "AND (:excludeId IS NULL OR r.id <> :excludeId) AND (" +
        "EXISTS (SELECT 1 FROM RepairRequestLine l WHERE l.repairRequest = r AND l.equipment.id = :eqId) OR " +
        "(r.equipment.id = :eqId AND NOT EXISTS (SELECT 1 FROM RepairRequestLine l2 WHERE l2.repairRequest = r))" +
        ")"
    )
    boolean existsActiveRepairForEquipmentExcluding(
        @Param("eqId") Long equipmentId,
        @Param("excludeId") Long excludeRepairRequestId,
        @Param("statuses") Collection<RepairRequestStatus> statuses
    );

    @Query(
        "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RepairRequest r WHERE r.status IN :statuses " +
        "AND (:excludeId IS NULL OR r.id <> :excludeId) AND " +
        "EXISTS (SELECT 1 FROM RepairRequestLine l WHERE l.repairRequest = r AND l.lineType = :consumableType AND l.assetItem.id = :assetItemId)"
    )
    boolean existsActiveRepairForAssetItemExcluding(
        @Param("assetItemId") Long assetItemId,
        @Param("excludeId") Long excludeRepairRequestId,
        @Param("statuses") Collection<RepairRequestStatus> statuses,
        @Param("consumableType") AssetManagementType consumableType
    );
}

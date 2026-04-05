package com.gindevp.app.repository;

import com.gindevp.app.domain.ReturnRequestLine;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnRequestLine entity.
 */
@Repository
public interface ReturnRequestLineRepository extends JpaRepository<ReturnRequestLine, Long> {
    List<ReturnRequestLine> findAllByRequest_Id(Long requestId);

    boolean existsByRequest_IdAndEquipment_Id(Long requestId, Long equipmentId);

    boolean existsByRequest_IdAndEquipment_IdAndIdNot(Long requestId, Long equipmentId, Long excludeLineId);

    boolean existsByRequest_IdAndAssetItem_IdAndLineType(Long requestId, Long assetItemId, AssetManagementType lineType);

    boolean existsByRequest_IdAndAssetItem_IdAndLineTypeAndIdNot(
        Long requestId,
        Long assetItemId,
        AssetManagementType lineType,
        Long excludeLineId
    );

    @Query(
        "SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END FROM ReturnRequestLine rl JOIN rl.request rr " +
        "WHERE rl.lineType = :deviceType AND rl.equipment.id = :eqId " +
        "AND rr.status IN :statuses AND (:excludeRequestId IS NULL OR rr.id <> :excludeRequestId)"
    )
    boolean existsOpenReturnLineForEquipmentExcluding(
        @Param("eqId") Long equipmentId,
        @Param("excludeRequestId") Long excludeReturnRequestId,
        @Param("statuses") Collection<ReturnRequestStatus> statuses,
        @Param("deviceType") AssetManagementType deviceType
    );

    @Query(
        "SELECT CASE WHEN COUNT(rl) > 0 THEN true ELSE false END FROM ReturnRequestLine rl JOIN rl.request rr " +
        "WHERE rl.lineType = :consumableType AND rl.assetItem.id = :assetItemId " +
        "AND rr.status IN :statuses AND (:excludeRequestId IS NULL OR rr.id <> :excludeRequestId)"
    )
    boolean existsOpenReturnLineForConsumableAssetItemExcluding(
        @Param("assetItemId") Long assetItemId,
        @Param("excludeRequestId") Long excludeReturnRequestId,
        @Param("statuses") Collection<ReturnRequestStatus> statuses,
        @Param("consumableType") AssetManagementType consumableType
    );
    default Optional<ReturnRequestLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReturnRequestLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReturnRequestLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select returnRequestLine from ReturnRequestLine returnRequestLine left join fetch returnRequestLine.request left join fetch returnRequestLine.assetItem left join fetch returnRequestLine.equipment",
        countQuery = "select count(returnRequestLine) from ReturnRequestLine returnRequestLine"
    )
    Page<ReturnRequestLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select returnRequestLine from ReturnRequestLine returnRequestLine left join fetch returnRequestLine.request left join fetch returnRequestLine.assetItem left join fetch returnRequestLine.equipment"
    )
    List<ReturnRequestLine> findAllWithToOneRelationships();

    @Query(
        "select returnRequestLine from ReturnRequestLine returnRequestLine left join fetch returnRequestLine.request left join fetch returnRequestLine.assetItem left join fetch returnRequestLine.equipment where returnRequestLine.id =:id"
    )
    Optional<ReturnRequestLine> findOneWithToOneRelationships(@Param("id") Long id);
}

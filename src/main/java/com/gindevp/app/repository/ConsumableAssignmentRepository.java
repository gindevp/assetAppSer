package com.gindevp.app.repository;

import com.gindevp.app.domain.ConsumableAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ConsumableAssignment entity.
 */
@Repository
public interface ConsumableAssignmentRepository extends JpaRepository<ConsumableAssignment, Long> {
    List<ConsumableAssignment> findByEmployee_IdAndAssetItem_IdOrderByIdAsc(Long employeeId, Long assetItemId);

    /** Bàn giao cấp phòng ban (không gắn NV cụ thể) */
    List<ConsumableAssignment> findByDepartment_IdAndAssetItem_IdAndEmployeeIsNullOrderByIdAsc(Long departmentId, Long assetItemId);

    /** Bàn giao theo vị trí (không NV, không PB) */
    List<ConsumableAssignment> findByLocation_IdAndAssetItem_IdAndEmployeeIsNullAndDepartmentIsNullOrderByIdAsc(Long locationId, Long assetItemId);

    /** Bàn giao toàn công ty (không PB, không vị trí) */
    List<ConsumableAssignment> findByAssetItem_IdAndEmployeeIsNullAndDepartmentIsNullAndLocationIsNullOrderByIdAsc(Long assetItemId);
    default Optional<ConsumableAssignment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ConsumableAssignment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ConsumableAssignment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select consumableAssignment from ConsumableAssignment consumableAssignment left join fetch consumableAssignment.assetItem left join fetch consumableAssignment.employee left join fetch consumableAssignment.department left join fetch consumableAssignment.location",
        countQuery = "select count(consumableAssignment) from ConsumableAssignment consumableAssignment"
    )
    Page<ConsumableAssignment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select consumableAssignment from ConsumableAssignment consumableAssignment left join fetch consumableAssignment.assetItem left join fetch consumableAssignment.employee left join fetch consumableAssignment.department left join fetch consumableAssignment.location"
    )
    List<ConsumableAssignment> findAllWithToOneRelationships();

    @Query(
        "select consumableAssignment from ConsumableAssignment consumableAssignment left join fetch consumableAssignment.assetItem left join fetch consumableAssignment.employee left join fetch consumableAssignment.department left join fetch consumableAssignment.location where consumableAssignment.id =:id"
    )
    Optional<ConsumableAssignment> findOneWithToOneRelationships(@Param("id") Long id);
}

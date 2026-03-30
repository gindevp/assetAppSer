package com.gindevp.app.repository;

import com.gindevp.app.domain.EquipmentAssignment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EquipmentAssignment entity.
 */
@Repository
public interface EquipmentAssignmentRepository extends JpaRepository<EquipmentAssignment, Long> {
    Optional<EquipmentAssignment> findFirstByEquipment_IdAndReturnedDateIsNullOrderByIdDesc(Long equipmentId);
    default Optional<EquipmentAssignment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<EquipmentAssignment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<EquipmentAssignment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select equipmentAssignment from EquipmentAssignment equipmentAssignment left join fetch equipmentAssignment.equipment left join fetch equipmentAssignment.employee left join fetch equipmentAssignment.department left join fetch equipmentAssignment.location",
        countQuery = "select count(equipmentAssignment) from EquipmentAssignment equipmentAssignment"
    )
    Page<EquipmentAssignment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select equipmentAssignment from EquipmentAssignment equipmentAssignment left join fetch equipmentAssignment.equipment left join fetch equipmentAssignment.employee left join fetch equipmentAssignment.department left join fetch equipmentAssignment.location"
    )
    List<EquipmentAssignment> findAllWithToOneRelationships();

    @Query(
        "select equipmentAssignment from EquipmentAssignment equipmentAssignment left join fetch equipmentAssignment.equipment left join fetch equipmentAssignment.employee left join fetch equipmentAssignment.department left join fetch equipmentAssignment.location where equipmentAssignment.id =:id"
    )
    Optional<EquipmentAssignment> findOneWithToOneRelationships(@Param("id") Long id);
}

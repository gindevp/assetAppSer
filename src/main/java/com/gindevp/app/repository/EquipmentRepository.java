package com.gindevp.app.repository;

import com.gindevp.app.domain.Equipment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Equipment entity.
 */
@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {
    default Optional<Equipment> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Equipment> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Equipment> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select equipment from Equipment equipment left join fetch equipment.assetItem left join fetch equipment.supplier",
        countQuery = "select count(equipment) from Equipment equipment"
    )
    Page<Equipment> findAllWithToOneRelationships(Pageable pageable);

    @Query("select equipment from Equipment equipment left join fetch equipment.assetItem left join fetch equipment.supplier")
    List<Equipment> findAllWithToOneRelationships();

    @Query(
        "select equipment from Equipment equipment left join fetch equipment.assetItem left join fetch equipment.supplier where equipment.id =:id"
    )
    Optional<Equipment> findOneWithToOneRelationships(@Param("id") Long id);
}

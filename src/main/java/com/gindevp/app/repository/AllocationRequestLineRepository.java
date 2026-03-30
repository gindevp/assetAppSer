package com.gindevp.app.repository;

import com.gindevp.app.domain.AllocationRequestLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AllocationRequestLine entity.
 */
@Repository
public interface AllocationRequestLineRepository extends JpaRepository<AllocationRequestLine, Long> {
    List<AllocationRequestLine> findAllByRequest_Id(Long requestId);
    default Optional<AllocationRequestLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AllocationRequestLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AllocationRequestLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select allocationRequestLine from AllocationRequestLine allocationRequestLine left join fetch allocationRequestLine.request left join fetch allocationRequestLine.assetItem left join fetch allocationRequestLine.assetLine left join fetch allocationRequestLine.equipment",
        countQuery = "select count(allocationRequestLine) from AllocationRequestLine allocationRequestLine"
    )
    Page<AllocationRequestLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select allocationRequestLine from AllocationRequestLine allocationRequestLine left join fetch allocationRequestLine.request left join fetch allocationRequestLine.assetItem left join fetch allocationRequestLine.assetLine left join fetch allocationRequestLine.equipment"
    )
    List<AllocationRequestLine> findAllWithToOneRelationships();

    @Query(
        "select allocationRequestLine from AllocationRequestLine allocationRequestLine left join fetch allocationRequestLine.request left join fetch allocationRequestLine.assetItem left join fetch allocationRequestLine.assetLine left join fetch allocationRequestLine.equipment where allocationRequestLine.id =:id"
    )
    Optional<AllocationRequestLine> findOneWithToOneRelationships(@Param("id") Long id);
}

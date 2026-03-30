package com.gindevp.app.repository;

import com.gindevp.app.domain.AssetLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssetLine entity.
 */
@Repository
public interface AssetLineRepository extends JpaRepository<AssetLine, Long> {
    default Optional<AssetLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AssetLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AssetLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assetLine from AssetLine assetLine left join fetch assetLine.assetGroup",
        countQuery = "select count(assetLine) from AssetLine assetLine"
    )
    Page<AssetLine> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assetLine from AssetLine assetLine left join fetch assetLine.assetGroup")
    List<AssetLine> findAllWithToOneRelationships();

    @Query("select assetLine from AssetLine assetLine left join fetch assetLine.assetGroup where assetLine.id =:id")
    Optional<AssetLine> findOneWithToOneRelationships(@Param("id") Long id);
}

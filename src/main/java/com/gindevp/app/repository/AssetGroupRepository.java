package com.gindevp.app.repository;

import com.gindevp.app.domain.AssetGroup;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssetGroup entity.
 */
@Repository
public interface AssetGroupRepository extends JpaRepository<AssetGroup, Long> {
    default Optional<AssetGroup> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AssetGroup> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AssetGroup> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assetGroup from AssetGroup assetGroup left join fetch assetGroup.assetType",
        countQuery = "select count(assetGroup) from AssetGroup assetGroup"
    )
    Page<AssetGroup> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assetGroup from AssetGroup assetGroup left join fetch assetGroup.assetType")
    List<AssetGroup> findAllWithToOneRelationships();

    @Query("select assetGroup from AssetGroup assetGroup left join fetch assetGroup.assetType where assetGroup.id =:id")
    Optional<AssetGroup> findOneWithToOneRelationships(@Param("id") Long id);
}

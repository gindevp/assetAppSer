package com.gindevp.app.repository;

import com.gindevp.app.domain.AssetItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AssetItem entity.
 */
@Repository
public interface AssetItemRepository extends JpaRepository<AssetItem, Long>, JpaSpecificationExecutor<AssetItem> {
    default Optional<AssetItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AssetItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AssetItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select assetItem from AssetItem assetItem left join fetch assetItem.assetLine",
        countQuery = "select count(assetItem) from AssetItem assetItem"
    )
    Page<AssetItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select assetItem from AssetItem assetItem left join fetch assetItem.assetLine")
    List<AssetItem> findAllWithToOneRelationships();

    @Query("select assetItem from AssetItem assetItem left join fetch assetItem.assetLine where assetItem.id =:id")
    Optional<AssetItem> findOneWithToOneRelationships(@Param("id") Long id);
}

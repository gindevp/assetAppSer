package com.gindevp.app.repository;

import com.gindevp.app.domain.ConsumableStock;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ConsumableStock entity.
 */
@Repository
public interface ConsumableStockRepository extends JpaRepository<ConsumableStock, Long> {
    Optional<ConsumableStock> findFirstByAssetItem_Id(Long assetItemId);

    /** Cùng mã vật tư có thể có nhiều bản ghi (dữ liệu cũ / nhập nhiều lần) — cộng tồn khi kiểm tra & trừ kho. */
    List<ConsumableStock> findAllByAssetItem_Id(Long assetItemId);
    default Optional<ConsumableStock> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ConsumableStock> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ConsumableStock> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select consumableStock from ConsumableStock consumableStock left join fetch consumableStock.assetItem",
        countQuery = "select count(consumableStock) from ConsumableStock consumableStock"
    )
    Page<ConsumableStock> findAllWithToOneRelationships(Pageable pageable);

    @Query("select consumableStock from ConsumableStock consumableStock left join fetch consumableStock.assetItem")
    List<ConsumableStock> findAllWithToOneRelationships();

    @Query(
        "select consumableStock from ConsumableStock consumableStock left join fetch consumableStock.assetItem where consumableStock.id =:id"
    )
    Optional<ConsumableStock> findOneWithToOneRelationships(@Param("id") Long id);
}

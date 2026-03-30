package com.gindevp.app.repository;

import com.gindevp.app.domain.StockReceiptLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockReceiptLine entity.
 */
@Repository
public interface StockReceiptLineRepository extends JpaRepository<StockReceiptLine, Long> {
    List<StockReceiptLine> findByReceipt_IdOrderByLineNoAsc(Long receiptId);

    default Optional<StockReceiptLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockReceiptLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockReceiptLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select stockReceiptLine from StockReceiptLine stockReceiptLine left join fetch stockReceiptLine.receipt left join fetch stockReceiptLine.assetItem",
        countQuery = "select count(stockReceiptLine) from StockReceiptLine stockReceiptLine"
    )
    Page<StockReceiptLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select stockReceiptLine from StockReceiptLine stockReceiptLine left join fetch stockReceiptLine.receipt left join fetch stockReceiptLine.assetItem"
    )
    List<StockReceiptLine> findAllWithToOneRelationships();

    @Query(
        "select stockReceiptLine from StockReceiptLine stockReceiptLine left join fetch stockReceiptLine.receipt left join fetch stockReceiptLine.assetItem where stockReceiptLine.id =:id"
    )
    Optional<StockReceiptLine> findOneWithToOneRelationships(@Param("id") Long id);
}

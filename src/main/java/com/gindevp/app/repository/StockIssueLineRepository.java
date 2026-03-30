package com.gindevp.app.repository;

import com.gindevp.app.domain.StockIssueLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockIssueLine entity.
 */
@Repository
public interface StockIssueLineRepository extends JpaRepository<StockIssueLine, Long> {
    List<StockIssueLine> findByIssue_IdOrderByLineNoAsc(Long issueId);

    default Optional<StockIssueLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockIssueLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockIssueLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select stockIssueLine from StockIssueLine stockIssueLine left join fetch stockIssueLine.issue left join fetch stockIssueLine.assetItem left join fetch stockIssueLine.equipment",
        countQuery = "select count(stockIssueLine) from StockIssueLine stockIssueLine"
    )
    Page<StockIssueLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select stockIssueLine from StockIssueLine stockIssueLine left join fetch stockIssueLine.issue left join fetch stockIssueLine.assetItem left join fetch stockIssueLine.equipment"
    )
    List<StockIssueLine> findAllWithToOneRelationships();

    @Query(
        "select stockIssueLine from StockIssueLine stockIssueLine left join fetch stockIssueLine.issue left join fetch stockIssueLine.assetItem left join fetch stockIssueLine.equipment where stockIssueLine.id =:id"
    )
    Optional<StockIssueLine> findOneWithToOneRelationships(@Param("id") Long id);
}

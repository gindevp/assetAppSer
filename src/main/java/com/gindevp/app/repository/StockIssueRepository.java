package com.gindevp.app.repository;

import com.gindevp.app.domain.StockIssue;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockIssue entity.
 */
@Repository
public interface StockIssueRepository extends JpaRepository<StockIssue, Long>, JpaSpecificationExecutor<StockIssue> {
    Optional<StockIssue> findByAllocationRequest_Id(Long allocationRequestId);

    boolean existsByCode(String code);

    default Optional<StockIssue> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<StockIssue> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<StockIssue> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value =
            "select stockIssue from StockIssue stockIssue left join fetch stockIssue.employee left join fetch stockIssue.department left join fetch stockIssue.location left join fetch stockIssue.allocationRequest",
        countQuery = "select count(stockIssue) from StockIssue stockIssue"
    )
    Page<StockIssue> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select stockIssue from StockIssue stockIssue left join fetch stockIssue.employee left join fetch stockIssue.department left join fetch stockIssue.location left join fetch stockIssue.allocationRequest"
    )
    List<StockIssue> findAllWithToOneRelationships();

    @Query(
        "select stockIssue from StockIssue stockIssue left join fetch stockIssue.employee left join fetch stockIssue.department left join fetch stockIssue.location left join fetch stockIssue.allocationRequest where stockIssue.id =:id"
    )
    Optional<StockIssue> findOneWithToOneRelationships(@Param("id") Long id);
}

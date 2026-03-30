package com.gindevp.app.repository;

import com.gindevp.app.domain.ReturnRequestLine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ReturnRequestLine entity.
 */
@Repository
public interface ReturnRequestLineRepository extends JpaRepository<ReturnRequestLine, Long> {
    List<ReturnRequestLine> findAllByRequest_Id(Long requestId);
    default Optional<ReturnRequestLine> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ReturnRequestLine> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ReturnRequestLine> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select returnRequestLine from ReturnRequestLine returnRequestLine left join fetch returnRequestLine.request left join fetch returnRequestLine.assetItem left join fetch returnRequestLine.equipment",
        countQuery = "select count(returnRequestLine) from ReturnRequestLine returnRequestLine"
    )
    Page<ReturnRequestLine> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select returnRequestLine from ReturnRequestLine returnRequestLine left join fetch returnRequestLine.request left join fetch returnRequestLine.assetItem left join fetch returnRequestLine.equipment"
    )
    List<ReturnRequestLine> findAllWithToOneRelationships();

    @Query(
        "select returnRequestLine from ReturnRequestLine returnRequestLine left join fetch returnRequestLine.request left join fetch returnRequestLine.assetItem left join fetch returnRequestLine.equipment where returnRequestLine.id =:id"
    )
    Optional<ReturnRequestLine> findOneWithToOneRelationships(@Param("id") Long id);
}

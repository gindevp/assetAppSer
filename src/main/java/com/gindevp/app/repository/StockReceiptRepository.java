package com.gindevp.app.repository;

import com.gindevp.app.domain.StockReceipt;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the StockReceipt entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StockReceiptRepository extends JpaRepository<StockReceipt, Long>, JpaSpecificationExecutor<StockReceipt> {}

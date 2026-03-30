package com.gindevp.app.repository;

import com.gindevp.app.domain.StockDocumentEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDocumentEventRepository extends JpaRepository<StockDocumentEvent, Long> {
    List<StockDocumentEvent> findByDocTypeAndDocIdOrderByOccurredAtDesc(String docType, Long docId);
}

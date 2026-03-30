package com.gindevp.app.service;

import com.gindevp.app.domain.StockDocumentEvent;
import com.gindevp.app.repository.StockDocumentEventRepository;
import com.gindevp.app.security.SecurityUtils;
import com.gindevp.app.service.dto.StockDocumentEventDTO;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockDocumentEventService {

    private static final Logger LOG = LoggerFactory.getLogger(StockDocumentEventService.class);

    public static final String DOC_RECEIPT = "RECEIPT";
    public static final String DOC_ISSUE = "ISSUE";

    private final StockDocumentEventRepository stockDocumentEventRepository;

    public StockDocumentEventService(StockDocumentEventRepository stockDocumentEventRepository) {
        this.stockDocumentEventRepository = stockDocumentEventRepository;
    }

    @Transactional
    public void record(String docType, Long docId, String action, String summary, String detail) {
        try {
            StockDocumentEvent row = new StockDocumentEvent();
            row.setOccurredAt(Instant.now());
            row.setLogin(SecurityUtils.getCurrentUserLogin().orElse("anonymous"));
            row.setDocType(docType);
            row.setDocId(docId);
            row.setAction(action);
            row.setSummary(truncate(summary, 500));
            row.setDetail(truncate(detail, 2000));
            stockDocumentEventRepository.save(row);
        } catch (Exception e) {
            LOG.warn("Stock document event skipped: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<StockDocumentEventDTO> findForReceipt(Long receiptId) {
        return toDtoList(stockDocumentEventRepository.findByDocTypeAndDocIdOrderByOccurredAtDesc(DOC_RECEIPT, receiptId));
    }

    @Transactional(readOnly = true)
    public List<StockDocumentEventDTO> findForIssue(Long issueId) {
        return toDtoList(stockDocumentEventRepository.findByDocTypeAndDocIdOrderByOccurredAtDesc(DOC_ISSUE, issueId));
    }

    private static List<StockDocumentEventDTO> toDtoList(List<StockDocumentEvent> rows) {
        return rows.stream().map(StockDocumentEventService::toDto).collect(Collectors.toList());
    }

    private static StockDocumentEventDTO toDto(StockDocumentEvent e) {
        StockDocumentEventDTO d = new StockDocumentEventDTO();
        d.setId(e.getId());
        d.setOccurredAt(e.getOccurredAt());
        d.setLogin(e.getLogin());
        d.setDocType(e.getDocType());
        d.setDocId(e.getDocId());
        d.setAction(e.getAction());
        d.setSummary(e.getSummary());
        d.setDetail(e.getDetail());
        return d;
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}

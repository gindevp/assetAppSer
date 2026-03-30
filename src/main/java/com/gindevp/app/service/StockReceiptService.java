package com.gindevp.app.service;

import com.gindevp.app.domain.StockReceipt;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.repository.StockReceiptRepository;
import com.gindevp.app.service.dto.StockReceiptDTO;
import com.gindevp.app.service.mapper.StockReceiptMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.StockReceipt}.
 */
@Service
@Transactional
public class StockReceiptService {

    private static final Logger LOG = LoggerFactory.getLogger(StockReceiptService.class);

    private final StockReceiptRepository stockReceiptRepository;

    private final StockReceiptMapper stockReceiptMapper;

    private final StockDocumentEventService stockDocumentEventService;

    public StockReceiptService(
        StockReceiptRepository stockReceiptRepository,
        StockReceiptMapper stockReceiptMapper,
        StockDocumentEventService stockDocumentEventService
    ) {
        this.stockReceiptRepository = stockReceiptRepository;
        this.stockReceiptMapper = stockReceiptMapper;
        this.stockDocumentEventService = stockDocumentEventService;
    }

    /**
     * Save a stockReceipt.
     *
     * @param stockReceiptDTO the entity to save.
     * @return the persisted entity.
     */
    public StockReceiptDTO save(StockReceiptDTO stockReceiptDTO) {
        LOG.debug("Request to save StockReceipt : {}", stockReceiptDTO);
        boolean isNew = stockReceiptDTO.getId() == null;
        StockReceipt stockReceipt = stockReceiptMapper.toEntity(stockReceiptDTO);
        stockReceipt = stockReceiptRepository.save(stockReceipt);
        StockReceiptDTO dto = stockReceiptMapper.toDto(stockReceipt);
        if (isNew && dto.getId() != null) {
            stockDocumentEventService.record(
                StockDocumentEventService.DOC_RECEIPT,
                dto.getId(),
                "CREATE",
                "Tạo phiếu nhập " + dto.getCode(),
                "status=" + dto.getStatus() + ", source=" + dto.getSource()
            );
        }
        return dto;
    }

    /**
     * Update a stockReceipt.
     *
     * @param stockReceiptDTO the entity to save.
     * @return the persisted entity.
     */
    public StockReceiptDTO update(StockReceiptDTO stockReceiptDTO) {
        LOG.debug("Request to update StockReceipt : {}", stockReceiptDTO);
        StockReceipt stockReceipt = stockReceiptMapper.toEntity(stockReceiptDTO);
        stockReceipt = stockReceiptRepository.save(stockReceipt);
        StockReceiptDTO dto = stockReceiptMapper.toDto(stockReceipt);
        if (dto.getId() != null) {
            stockDocumentEventService.record(
                StockDocumentEventService.DOC_RECEIPT,
                dto.getId(),
                "UPDATE",
                "Cập nhật phiếu nhập " + dto.getCode(),
                "status=" + dto.getStatus()
            );
        }
        return dto;
    }

    /**
     * Partially update a stockReceipt.
     *
     * @param stockReceiptDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockReceiptDTO> partialUpdate(StockReceiptDTO stockReceiptDTO) {
        LOG.debug("Request to partially update StockReceipt : {}", stockReceiptDTO);

        return stockReceiptRepository.findById(stockReceiptDTO.getId()).map(existingStockReceipt -> {
            DocumentStatus oldStatus = existingStockReceipt.getStatus();
            stockReceiptMapper.partialUpdate(existingStockReceipt, stockReceiptDTO);
            StockReceipt saved = stockReceiptRepository.save(existingStockReceipt);
            StockReceiptDTO dto = stockReceiptMapper.toDto(saved);
            if (dto.getId() != null) {
                boolean statusChanged = stockReceiptDTO.getStatus() != null && oldStatus != saved.getStatus();
                if (statusChanged) {
                    stockDocumentEventService.record(
                        StockDocumentEventService.DOC_RECEIPT,
                        dto.getId(),
                        "STATUS_CHANGE",
                        "Đổi trạng thái phiếu nhập " + dto.getCode(),
                        oldStatus + " → " + saved.getStatus()
                    );
                } else {
                    stockDocumentEventService.record(
                        StockDocumentEventService.DOC_RECEIPT,
                        dto.getId(),
                        "UPDATE",
                        "Cập nhật phiếu nhập " + dto.getCode(),
                        null
                    );
                }
            }
            return dto;
        });
    }

    /**
     * Get one stockReceipt by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockReceiptDTO> findOne(Long id) {
        LOG.debug("Request to get StockReceipt : {}", id);
        return stockReceiptRepository.findById(id).map(stockReceiptMapper::toDto);
    }

    /**
     * Delete the stockReceipt by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StockReceipt : {}", id);
        stockReceiptRepository
            .findById(id)
            .ifPresent(r ->
                stockDocumentEventService.record(
                    StockDocumentEventService.DOC_RECEIPT,
                    id,
                    "DELETE",
                    "Xóa phiếu nhập " + r.getCode(),
                    null
                )
            );
        stockReceiptRepository.deleteById(id);
    }
}

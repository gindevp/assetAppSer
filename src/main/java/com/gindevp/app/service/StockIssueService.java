package com.gindevp.app.service;

import com.gindevp.app.domain.StockIssue;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.repository.StockIssueRepository;
import com.gindevp.app.service.dto.StockIssueDTO;
import com.gindevp.app.service.mapper.StockIssueMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.StockIssue}.
 */
@Service
@Transactional
public class StockIssueService {

    private static final Logger LOG = LoggerFactory.getLogger(StockIssueService.class);

    private final StockIssueRepository stockIssueRepository;

    private final StockIssueMapper stockIssueMapper;

    private final StockDocumentEventService stockDocumentEventService;

    public StockIssueService(
        StockIssueRepository stockIssueRepository,
        StockIssueMapper stockIssueMapper,
        StockDocumentEventService stockDocumentEventService
    ) {
        this.stockIssueRepository = stockIssueRepository;
        this.stockIssueMapper = stockIssueMapper;
        this.stockDocumentEventService = stockDocumentEventService;
    }

    /**
     * Save a stockIssue.
     *
     * @param stockIssueDTO the entity to save.
     * @return the persisted entity.
     */
    public StockIssueDTO save(StockIssueDTO stockIssueDTO) {
        LOG.debug("Request to save StockIssue : {}", stockIssueDTO);
        boolean isNew = stockIssueDTO.getId() == null;
        StockIssue stockIssue = stockIssueMapper.toEntity(stockIssueDTO);
        stockIssue = stockIssueRepository.save(stockIssue);
        StockIssueDTO dto = stockIssueMapper.toDto(stockIssue);
        if (isNew && dto.getId() != null) {
            stockDocumentEventService.record(
                StockDocumentEventService.DOC_ISSUE,
                dto.getId(),
                "CREATE",
                "Tạo phiếu xuất " + dto.getCode(),
                "status=" + dto.getStatus() + ", assigneeType=" + dto.getAssigneeType()
            );
        }
        return dto;
    }

    /**
     * Update a stockIssue.
     *
     * @param stockIssueDTO the entity to save.
     * @return the persisted entity.
     */
    public StockIssueDTO update(StockIssueDTO stockIssueDTO) {
        LOG.debug("Request to update StockIssue : {}", stockIssueDTO);
        StockIssue stockIssue = stockIssueMapper.toEntity(stockIssueDTO);
        stockIssue = stockIssueRepository.save(stockIssue);
        StockIssueDTO dto = stockIssueMapper.toDto(stockIssue);
        if (dto.getId() != null) {
            stockDocumentEventService.record(
                StockDocumentEventService.DOC_ISSUE,
                dto.getId(),
                "UPDATE",
                "Cập nhật phiếu xuất " + dto.getCode(),
                "status=" + dto.getStatus()
            );
        }
        return dto;
    }

    /**
     * Partially update a stockIssue.
     *
     * @param stockIssueDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockIssueDTO> partialUpdate(StockIssueDTO stockIssueDTO) {
        LOG.debug("Request to partially update StockIssue : {}", stockIssueDTO);

        return stockIssueRepository.findById(stockIssueDTO.getId()).map(existingStockIssue -> {
            DocumentStatus oldStatus = existingStockIssue.getStatus();
            stockIssueMapper.partialUpdate(existingStockIssue, stockIssueDTO);
            StockIssue saved = stockIssueRepository.save(existingStockIssue);
            StockIssueDTO dto = stockIssueMapper.toDto(saved);
            if (dto.getId() != null) {
                boolean statusChanged = stockIssueDTO.getStatus() != null && oldStatus != saved.getStatus();
                if (statusChanged) {
                    stockDocumentEventService.record(
                        StockDocumentEventService.DOC_ISSUE,
                        dto.getId(),
                        "STATUS_CHANGE",
                        "Đổi trạng thái phiếu xuất " + dto.getCode(),
                        oldStatus + " → " + saved.getStatus()
                    );
                } else {
                    stockDocumentEventService.record(
                        StockDocumentEventService.DOC_ISSUE,
                        dto.getId(),
                        "UPDATE",
                        "Cập nhật phiếu xuất " + dto.getCode(),
                        null
                    );
                }
            }
            return dto;
        });
    }

    /**
     * Get all the stockIssues with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockIssueDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockIssueRepository.findAllWithEagerRelationships(pageable).map(stockIssueMapper::toDto);
    }

    /**
     * Get one stockIssue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockIssueDTO> findOne(Long id) {
        LOG.debug("Request to get StockIssue : {}", id);
        return stockIssueRepository.findOneWithEagerRelationships(id).map(stockIssueMapper::toDto);
    }

    /**
     * Delete the stockIssue by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StockIssue : {}", id);
        stockIssueRepository
            .findById(id)
            .ifPresent(i ->
                stockDocumentEventService.record(
                    StockDocumentEventService.DOC_ISSUE,
                    id,
                    "DELETE",
                    "Xóa phiếu xuất " + i.getCode(),
                    null
                )
            );
        stockIssueRepository.deleteById(id);
    }
}

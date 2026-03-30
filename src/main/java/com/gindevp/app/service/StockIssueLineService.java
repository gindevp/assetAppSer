package com.gindevp.app.service;

import com.gindevp.app.domain.StockIssueLine;
import com.gindevp.app.repository.StockIssueLineRepository;
import com.gindevp.app.service.dto.StockIssueLineDTO;
import com.gindevp.app.service.mapper.StockIssueLineMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.StockIssueLine}.
 */
@Service
@Transactional
public class StockIssueLineService {

    private static final Logger LOG = LoggerFactory.getLogger(StockIssueLineService.class);

    private final StockIssueLineRepository stockIssueLineRepository;

    private final StockIssueLineMapper stockIssueLineMapper;

    public StockIssueLineService(StockIssueLineRepository stockIssueLineRepository, StockIssueLineMapper stockIssueLineMapper) {
        this.stockIssueLineRepository = stockIssueLineRepository;
        this.stockIssueLineMapper = stockIssueLineMapper;
    }

    /**
     * Save a stockIssueLine.
     *
     * @param stockIssueLineDTO the entity to save.
     * @return the persisted entity.
     */
    public StockIssueLineDTO save(StockIssueLineDTO stockIssueLineDTO) {
        LOG.debug("Request to save StockIssueLine : {}", stockIssueLineDTO);
        StockIssueLine stockIssueLine = stockIssueLineMapper.toEntity(stockIssueLineDTO);
        stockIssueLine = stockIssueLineRepository.save(stockIssueLine);
        return stockIssueLineMapper.toDto(stockIssueLine);
    }

    /**
     * Update a stockIssueLine.
     *
     * @param stockIssueLineDTO the entity to save.
     * @return the persisted entity.
     */
    public StockIssueLineDTO update(StockIssueLineDTO stockIssueLineDTO) {
        LOG.debug("Request to update StockIssueLine : {}", stockIssueLineDTO);
        StockIssueLine stockIssueLine = stockIssueLineMapper.toEntity(stockIssueLineDTO);
        stockIssueLine = stockIssueLineRepository.save(stockIssueLine);
        return stockIssueLineMapper.toDto(stockIssueLine);
    }

    /**
     * Partially update a stockIssueLine.
     *
     * @param stockIssueLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockIssueLineDTO> partialUpdate(StockIssueLineDTO stockIssueLineDTO) {
        LOG.debug("Request to partially update StockIssueLine : {}", stockIssueLineDTO);

        return stockIssueLineRepository
            .findById(stockIssueLineDTO.getId())
            .map(existingStockIssueLine -> {
                stockIssueLineMapper.partialUpdate(existingStockIssueLine, stockIssueLineDTO);

                return existingStockIssueLine;
            })
            .map(stockIssueLineRepository::save)
            .map(stockIssueLineMapper::toDto);
    }

    /**
     * Get all the stockIssueLines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockIssueLineDTO> findAll() {
        LOG.debug("Request to get all StockIssueLines");
        return stockIssueLineRepository
            .findAll()
            .stream()
            .map(stockIssueLineMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the stockIssueLines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockIssueLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockIssueLineRepository.findAllWithEagerRelationships(pageable).map(stockIssueLineMapper::toDto);
    }

    /**
     * Get one stockIssueLine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockIssueLineDTO> findOne(Long id) {
        LOG.debug("Request to get StockIssueLine : {}", id);
        return stockIssueLineRepository.findOneWithEagerRelationships(id).map(stockIssueLineMapper::toDto);
    }

    /**
     * Delete the stockIssueLine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StockIssueLine : {}", id);
        stockIssueLineRepository.deleteById(id);
    }
}

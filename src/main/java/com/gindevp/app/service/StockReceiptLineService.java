package com.gindevp.app.service;

import com.gindevp.app.domain.StockReceiptLine;
import com.gindevp.app.repository.StockReceiptLineRepository;
import com.gindevp.app.service.dto.StockReceiptLineDTO;
import com.gindevp.app.service.mapper.StockReceiptLineMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.StockReceiptLine}.
 */
@Service
@Transactional
public class StockReceiptLineService {

    private static final Logger LOG = LoggerFactory.getLogger(StockReceiptLineService.class);

    private final StockReceiptLineRepository stockReceiptLineRepository;

    private final StockReceiptLineMapper stockReceiptLineMapper;

    public StockReceiptLineService(StockReceiptLineRepository stockReceiptLineRepository, StockReceiptLineMapper stockReceiptLineMapper) {
        this.stockReceiptLineRepository = stockReceiptLineRepository;
        this.stockReceiptLineMapper = stockReceiptLineMapper;
    }

    /**
     * Save a stockReceiptLine.
     *
     * @param stockReceiptLineDTO the entity to save.
     * @return the persisted entity.
     */
    public StockReceiptLineDTO save(StockReceiptLineDTO stockReceiptLineDTO) {
        LOG.debug("Request to save StockReceiptLine : {}", stockReceiptLineDTO);
        StockReceiptLine stockReceiptLine = stockReceiptLineMapper.toEntity(stockReceiptLineDTO);
        stockReceiptLine = stockReceiptLineRepository.save(stockReceiptLine);
        return stockReceiptLineMapper.toDto(stockReceiptLine);
    }

    /**
     * Update a stockReceiptLine.
     *
     * @param stockReceiptLineDTO the entity to save.
     * @return the persisted entity.
     */
    public StockReceiptLineDTO update(StockReceiptLineDTO stockReceiptLineDTO) {
        LOG.debug("Request to update StockReceiptLine : {}", stockReceiptLineDTO);
        StockReceiptLine stockReceiptLine = stockReceiptLineMapper.toEntity(stockReceiptLineDTO);
        stockReceiptLine = stockReceiptLineRepository.save(stockReceiptLine);
        return stockReceiptLineMapper.toDto(stockReceiptLine);
    }

    /**
     * Partially update a stockReceiptLine.
     *
     * @param stockReceiptLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<StockReceiptLineDTO> partialUpdate(StockReceiptLineDTO stockReceiptLineDTO) {
        LOG.debug("Request to partially update StockReceiptLine : {}", stockReceiptLineDTO);

        return stockReceiptLineRepository
            .findById(stockReceiptLineDTO.getId())
            .map(existingStockReceiptLine -> {
                stockReceiptLineMapper.partialUpdate(existingStockReceiptLine, stockReceiptLineDTO);

                return existingStockReceiptLine;
            })
            .map(stockReceiptLineRepository::save)
            .map(stockReceiptLineMapper::toDto);
    }

    /**
     * Get all the stockReceiptLines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<StockReceiptLineDTO> findAll() {
        LOG.debug("Request to get all StockReceiptLines");
        return stockReceiptLineRepository
            .findAll()
            .stream()
            .map(stockReceiptLineMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the stockReceiptLines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<StockReceiptLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return stockReceiptLineRepository.findAllWithEagerRelationships(pageable).map(stockReceiptLineMapper::toDto);
    }

    /**
     * Get one stockReceiptLine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<StockReceiptLineDTO> findOne(Long id) {
        LOG.debug("Request to get StockReceiptLine : {}", id);
        return stockReceiptLineRepository.findOneWithEagerRelationships(id).map(stockReceiptLineMapper::toDto);
    }

    /**
     * Delete the stockReceiptLine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete StockReceiptLine : {}", id);
        stockReceiptLineRepository.deleteById(id);
    }
}

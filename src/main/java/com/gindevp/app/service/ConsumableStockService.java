package com.gindevp.app.service;

import com.gindevp.app.domain.ConsumableStock;
import com.gindevp.app.repository.ConsumableStockRepository;
import com.gindevp.app.service.dto.ConsumableStockDTO;
import com.gindevp.app.service.mapper.ConsumableStockMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.ConsumableStock}.
 */
@Service
@Transactional
public class ConsumableStockService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumableStockService.class);

    private final ConsumableStockRepository consumableStockRepository;

    private final ConsumableStockMapper consumableStockMapper;

    public ConsumableStockService(ConsumableStockRepository consumableStockRepository, ConsumableStockMapper consumableStockMapper) {
        this.consumableStockRepository = consumableStockRepository;
        this.consumableStockMapper = consumableStockMapper;
    }

    /**
     * Save a consumableStock.
     *
     * @param consumableStockDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsumableStockDTO save(ConsumableStockDTO consumableStockDTO) {
        LOG.debug("Request to save ConsumableStock : {}", consumableStockDTO);
        ConsumableStock consumableStock = consumableStockMapper.toEntity(consumableStockDTO);
        consumableStock = consumableStockRepository.save(consumableStock);
        return consumableStockMapper.toDto(consumableStock);
    }

    /**
     * Update a consumableStock.
     *
     * @param consumableStockDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsumableStockDTO update(ConsumableStockDTO consumableStockDTO) {
        LOG.debug("Request to update ConsumableStock : {}", consumableStockDTO);
        ConsumableStock consumableStock = consumableStockMapper.toEntity(consumableStockDTO);
        consumableStock = consumableStockRepository.save(consumableStock);
        return consumableStockMapper.toDto(consumableStock);
    }

    /**
     * Partially update a consumableStock.
     *
     * @param consumableStockDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConsumableStockDTO> partialUpdate(ConsumableStockDTO consumableStockDTO) {
        LOG.debug("Request to partially update ConsumableStock : {}", consumableStockDTO);

        return consumableStockRepository
            .findById(consumableStockDTO.getId())
            .map(existingConsumableStock -> {
                consumableStockMapper.partialUpdate(existingConsumableStock, consumableStockDTO);

                return existingConsumableStock;
            })
            .map(consumableStockRepository::save)
            .map(consumableStockMapper::toDto);
    }

    /**
     * Get all the consumableStocks.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ConsumableStockDTO> findAll() {
        LOG.debug("Request to get all ConsumableStocks");
        /** Join fetch assetItem — tránh lazy null / thiếu id khi serialize JSON cho FE ghép mã/tên vật tư */
        return consumableStockRepository
            .findAllWithToOneRelationships()
            .stream()
            .map(consumableStockMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the consumableStocks with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ConsumableStockDTO> findAllWithEagerRelationships(Pageable pageable) {
        return consumableStockRepository.findAllWithEagerRelationships(pageable).map(consumableStockMapper::toDto);
    }

    /**
     * Get one consumableStock by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ConsumableStockDTO> findOne(Long id) {
        LOG.debug("Request to get ConsumableStock : {}", id);
        return consumableStockRepository.findOneWithEagerRelationships(id).map(consumableStockMapper::toDto);
    }

    /**
     * Delete the consumableStock by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ConsumableStock : {}", id);
        consumableStockRepository.deleteById(id);
    }
}

package com.gindevp.app.service;

import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.repository.AssetLineRepository;
import com.gindevp.app.service.dto.AssetLineDTO;
import com.gindevp.app.service.mapper.AssetLineMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.AssetLine}.
 */
@Service
@Transactional
public class AssetLineService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetLineService.class);

    private final AssetLineRepository assetLineRepository;

    private final AssetLineMapper assetLineMapper;

    public AssetLineService(AssetLineRepository assetLineRepository, AssetLineMapper assetLineMapper) {
        this.assetLineRepository = assetLineRepository;
        this.assetLineMapper = assetLineMapper;
    }

    /**
     * Save a assetLine.
     *
     * @param assetLineDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetLineDTO save(AssetLineDTO assetLineDTO) {
        LOG.debug("Request to save AssetLine : {}", assetLineDTO);
        AssetLine assetLine = assetLineMapper.toEntity(assetLineDTO);
        assetLine = assetLineRepository.save(assetLine);
        return assetLineMapper.toDto(assetLine);
    }

    /**
     * Update a assetLine.
     *
     * @param assetLineDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetLineDTO update(AssetLineDTO assetLineDTO) {
        LOG.debug("Request to update AssetLine : {}", assetLineDTO);
        AssetLine assetLine = assetLineMapper.toEntity(assetLineDTO);
        assetLine = assetLineRepository.save(assetLine);
        return assetLineMapper.toDto(assetLine);
    }

    /**
     * Partially update a assetLine.
     *
     * @param assetLineDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssetLineDTO> partialUpdate(AssetLineDTO assetLineDTO) {
        LOG.debug("Request to partially update AssetLine : {}", assetLineDTO);

        return assetLineRepository
            .findById(assetLineDTO.getId())
            .map(existingAssetLine -> {
                assetLineMapper.partialUpdate(existingAssetLine, assetLineDTO);

                return existingAssetLine;
            })
            .map(assetLineRepository::save)
            .map(assetLineMapper::toDto);
    }

    /**
     * Get all the assetLines.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AssetLineDTO> findAll() {
        LOG.debug("Request to get all AssetLines");
        return assetLineRepository
            .findAllWithToOneRelationships()
            .stream()
            .map(assetLineMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the assetLines with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AssetLineDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assetLineRepository.findAllWithEagerRelationships(pageable).map(assetLineMapper::toDto);
    }

    /**
     * Get one assetLine by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssetLineDTO> findOne(Long id) {
        LOG.debug("Request to get AssetLine : {}", id);
        return assetLineRepository.findOneWithEagerRelationships(id).map(assetLineMapper::toDto);
    }

    /**
     * Delete the assetLine by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AssetLine : {}", id);
        assetLineRepository.deleteById(id);
    }
}

package com.gindevp.app.service;

import com.gindevp.app.domain.AssetType;
import com.gindevp.app.repository.AssetTypeRepository;
import com.gindevp.app.service.dto.AssetTypeDTO;
import com.gindevp.app.service.mapper.AssetTypeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.AssetType}.
 */
@Service
@Transactional
public class AssetTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetTypeService.class);

    private final AssetTypeRepository assetTypeRepository;

    private final AssetTypeMapper assetTypeMapper;

    public AssetTypeService(AssetTypeRepository assetTypeRepository, AssetTypeMapper assetTypeMapper) {
        this.assetTypeRepository = assetTypeRepository;
        this.assetTypeMapper = assetTypeMapper;
    }

    /**
     * Save a assetType.
     *
     * @param assetTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetTypeDTO save(AssetTypeDTO assetTypeDTO) {
        LOG.debug("Request to save AssetType : {}", assetTypeDTO);
        AssetType assetType = assetTypeMapper.toEntity(assetTypeDTO);
        assetType = assetTypeRepository.save(assetType);
        return assetTypeMapper.toDto(assetType);
    }

    /**
     * Update a assetType.
     *
     * @param assetTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetTypeDTO update(AssetTypeDTO assetTypeDTO) {
        LOG.debug("Request to update AssetType : {}", assetTypeDTO);
        AssetType assetType = assetTypeMapper.toEntity(assetTypeDTO);
        assetType = assetTypeRepository.save(assetType);
        return assetTypeMapper.toDto(assetType);
    }

    /**
     * Partially update a assetType.
     *
     * @param assetTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssetTypeDTO> partialUpdate(AssetTypeDTO assetTypeDTO) {
        LOG.debug("Request to partially update AssetType : {}", assetTypeDTO);

        return assetTypeRepository
            .findById(assetTypeDTO.getId())
            .map(existingAssetType -> {
                assetTypeMapper.partialUpdate(existingAssetType, assetTypeDTO);

                return existingAssetType;
            })
            .map(assetTypeRepository::save)
            .map(assetTypeMapper::toDto);
    }

    /**
     * Get all the assetTypes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AssetTypeDTO> findAll() {
        LOG.debug("Request to get all AssetTypes");
        return assetTypeRepository.findAll().stream().map(assetTypeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one assetType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssetTypeDTO> findOne(Long id) {
        LOG.debug("Request to get AssetType : {}", id);
        return assetTypeRepository.findById(id).map(assetTypeMapper::toDto);
    }

    /**
     * Delete the assetType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AssetType : {}", id);
        assetTypeRepository.deleteById(id);
    }
}

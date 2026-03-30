package com.gindevp.app.service;

import com.gindevp.app.domain.AssetGroup;
import com.gindevp.app.repository.AssetGroupRepository;
import com.gindevp.app.service.dto.AssetGroupDTO;
import com.gindevp.app.service.mapper.AssetGroupMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.AssetGroup}.
 */
@Service
@Transactional
public class AssetGroupService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupService.class);

    private final AssetGroupRepository assetGroupRepository;

    private final AssetGroupMapper assetGroupMapper;

    public AssetGroupService(AssetGroupRepository assetGroupRepository, AssetGroupMapper assetGroupMapper) {
        this.assetGroupRepository = assetGroupRepository;
        this.assetGroupMapper = assetGroupMapper;
    }

    /**
     * Save a assetGroup.
     *
     * @param assetGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetGroupDTO save(AssetGroupDTO assetGroupDTO) {
        LOG.debug("Request to save AssetGroup : {}", assetGroupDTO);
        AssetGroup assetGroup = assetGroupMapper.toEntity(assetGroupDTO);
        assetGroup = assetGroupRepository.save(assetGroup);
        return assetGroupMapper.toDto(assetGroup);
    }

    /**
     * Update a assetGroup.
     *
     * @param assetGroupDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetGroupDTO update(AssetGroupDTO assetGroupDTO) {
        LOG.debug("Request to update AssetGroup : {}", assetGroupDTO);
        AssetGroup assetGroup = assetGroupMapper.toEntity(assetGroupDTO);
        assetGroup = assetGroupRepository.save(assetGroup);
        return assetGroupMapper.toDto(assetGroup);
    }

    /**
     * Partially update a assetGroup.
     *
     * @param assetGroupDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssetGroupDTO> partialUpdate(AssetGroupDTO assetGroupDTO) {
        LOG.debug("Request to partially update AssetGroup : {}", assetGroupDTO);

        return assetGroupRepository
            .findById(assetGroupDTO.getId())
            .map(existingAssetGroup -> {
                assetGroupMapper.partialUpdate(existingAssetGroup, assetGroupDTO);

                return existingAssetGroup;
            })
            .map(assetGroupRepository::save)
            .map(assetGroupMapper::toDto);
    }

    /**
     * Get all the assetGroups.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<AssetGroupDTO> findAll() {
        LOG.debug("Request to get all AssetGroups");
        return assetGroupRepository.findAll().stream().map(assetGroupMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the assetGroups with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AssetGroupDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assetGroupRepository.findAllWithEagerRelationships(pageable).map(assetGroupMapper::toDto);
    }

    /**
     * Get one assetGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssetGroupDTO> findOne(Long id) {
        LOG.debug("Request to get AssetGroup : {}", id);
        return assetGroupRepository.findOneWithEagerRelationships(id).map(assetGroupMapper::toDto);
    }

    /**
     * Delete the assetGroup by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AssetGroup : {}", id);
        assetGroupRepository.deleteById(id);
    }
}

package com.gindevp.app.service;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.mapper.AssetItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.AssetItem}.
 */
@Service
@Transactional
public class AssetItemService {

    private static final Logger LOG = LoggerFactory.getLogger(AssetItemService.class);

    private final AssetItemRepository assetItemRepository;

    private final AssetItemMapper assetItemMapper;

    public AssetItemService(AssetItemRepository assetItemRepository, AssetItemMapper assetItemMapper) {
        this.assetItemRepository = assetItemRepository;
        this.assetItemMapper = assetItemMapper;
    }

    /**
     * Save a assetItem.
     *
     * @param assetItemDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetItemDTO save(AssetItemDTO assetItemDTO) {
        LOG.debug("Request to save AssetItem : {}", assetItemDTO);
        AssetItem assetItem = assetItemMapper.toEntity(assetItemDTO);
        normalizeDeviceFlags(assetItem);
        assetItem = assetItemRepository.save(assetItem);
        return assetItemMapper.toDto(assetItem);
    }

    /**
     * Update a assetItem.
     *
     * @param assetItemDTO the entity to save.
     * @return the persisted entity.
     */
    public AssetItemDTO update(AssetItemDTO assetItemDTO) {
        LOG.debug("Request to update AssetItem : {}", assetItemDTO);
        AssetItem assetItem = assetItemMapper.toEntity(assetItemDTO);
        normalizeDeviceFlags(assetItem);
        assetItem = assetItemRepository.save(assetItem);
        return assetItemMapper.toDto(assetItem);
    }

    /**
     * Partially update a assetItem.
     *
     * @param assetItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<AssetItemDTO> partialUpdate(AssetItemDTO assetItemDTO) {
        LOG.debug("Request to partially update AssetItem : {}", assetItemDTO);

        return assetItemRepository
            .findById(assetItemDTO.getId())
            .map(existingAssetItem -> {
                assetItemMapper.partialUpdate(existingAssetItem, assetItemDTO);
                normalizeDeviceFlags(existingAssetItem);

                return existingAssetItem;
            })
            .map(assetItemRepository::save)
            .map(assetItemMapper::toDto);
    }

    /**
     * Get all the assetItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<AssetItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return assetItemRepository.findAllWithEagerRelationships(pageable).map(assetItemMapper::toDto);
    }

    /**
     * Get one assetItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<AssetItemDTO> findOne(Long id) {
        LOG.debug("Request to get AssetItem : {}", id);
        return assetItemRepository.findOneWithEagerRelationships(id).map(assetItemMapper::toDto);
    }

    /**
     * Delete the assetItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete AssetItem : {}", id);
        assetItemRepository.deleteById(id);
    }

    /**
     * Nghiệp vụ:
     * - Thiết bị (DEVICE): cho phép cấu hình khấu hao/serial theo từng item (mặc định true nếu chưa set).
     * - Vật tư (CONSUMABLE): không dùng khấu hao/serial tracking ở cấp item.
     */
    private static void normalizeDeviceFlags(AssetItem item) {
        if (item == null || item.getManagementType() == null) return;
        if (item.getManagementType() == AssetManagementType.DEVICE) {
            if (item.getDepreciationEnabled() == null) {
                item.setDepreciationEnabled(true);
            }
            if (item.getSerialTrackingRequired() == null) {
                item.setSerialTrackingRequired(true);
            }
        } else {
            item.setDepreciationEnabled(false);
            item.setSerialTrackingRequired(false);
        }
    }
}

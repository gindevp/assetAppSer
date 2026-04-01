package com.gindevp.app.service;

import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.mapper.EquipmentMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.gindevp.app.domain.Equipment}.
 */
@Service
@Transactional
public class EquipmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentService.class);

    private static final String ENTITY_NAME = "equipment";

    private final AssetItemRepository assetItemRepository;

    private final EquipmentRepository equipmentRepository;

    private final EquipmentMapper equipmentMapper;

    public EquipmentService(AssetItemRepository assetItemRepository, EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.assetItemRepository = assetItemRepository;
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    /**
     * Save a equipment.
     *
     * @param equipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipmentDTO save(EquipmentDTO equipmentDTO) {
        LOG.debug("Request to save Equipment : {}", equipmentDTO);
        assertEquipmentCodeFormat(equipmentDTO.getEquipmentCode());
        validateBusinessRules(equipmentDTO, null);
        Equipment equipment = equipmentMapper.toEntity(equipmentDTO);
        equipment = equipmentRepository.save(equipment);
        return equipmentMapper.toDto(equipment);
    }

    /**
     * Update a equipment.
     *
     * @param equipmentDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipmentDTO update(EquipmentDTO equipmentDTO) {
        LOG.debug("Request to update Equipment : {}", equipmentDTO);
        assertEquipmentCodeFormat(equipmentDTO.getEquipmentCode());
        validateBusinessRules(equipmentDTO, null);
        Equipment equipment = equipmentMapper.toEntity(equipmentDTO);
        equipment = equipmentRepository.save(equipment);
        return equipmentMapper.toDto(equipment);
    }

    /**
     * Partially update a equipment.
     *
     * @param equipmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EquipmentDTO> partialUpdate(EquipmentDTO equipmentDTO) {
        LOG.debug("Request to partially update Equipment : {}", equipmentDTO);

        return equipmentRepository
            .findById(equipmentDTO.getId())
            .map(existingEquipment -> {
                if (equipmentDTO.getEquipmentCode() != null) {
                    assertEquipmentCodeFormat(equipmentDTO.getEquipmentCode());
                }
                validateBusinessRules(equipmentDTO, existingEquipment);
                equipmentMapper.partialUpdate(existingEquipment, equipmentDTO);

                return existingEquipment;
            })
            .map(equipmentRepository::save)
            .map(equipmentMapper::toDto);
    }

    /**
     * Get all the equipment with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EquipmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return equipmentRepository.findAllWithEagerRelationships(pageable).map(equipmentMapper::toDto);
    }

    /**
     * Get one equipment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EquipmentDTO> findOne(Long id) {
        LOG.debug("Request to get Equipment : {}", id);
        return equipmentRepository.findOneWithEagerRelationships(id).map(equipmentMapper::toDto);
    }

    /**
     * Delete the equipment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Equipment : {}", id);
        equipmentRepository.deleteById(id);
    }

    private static void assertEquipmentCodeFormat(String equipmentCode) {
        if (equipmentCode == null || !equipmentCode.matches("^EQ\\d{6}$")) {
            throw new BadRequestAlertException(
                "Mã thiết bị phải có dạng EQ + đúng 6 chữ số (vd EQ000001)",
                ENTITY_NAME,
                "invalidequipmentcode"
            );
        }
    }

    /**
     * Nghiệp vụ:
     * - Equipment luôn phải gắn với AssetItem quản lý THIẾT BỊ (managementType=DEVICE)
     * - Thiết bị phải có serial (khi item yêu cầu theo dõi serial)
     * - Thiết bị có khấu hao: bắt buộc có nguyên giá, ngày vốn hóa, số tháng khấu hao.
     */
    private void validateBusinessRules(EquipmentDTO dto, Equipment existing) {
        final Long assetItemId =
            dto != null && dto.getAssetItem() != null && dto.getAssetItem().getId() != null
                ? dto.getAssetItem().getId()
                : existing != null && existing.getAssetItem() != null && existing.getAssetItem().getId() != null
                    ? existing.getAssetItem().getId()
                    : null;

        if (assetItemId == null) {
            throw new BadRequestAlertException("Thiếu item tài sản cho thiết bị", ENTITY_NAME, "noassetitem");
        }

        var item = assetItemRepository
            .findById(assetItemId)
            .orElseThrow(() -> new BadRequestAlertException("Không tìm thấy item tài sản", ENTITY_NAME, "assetitemnotfound"));

        if (item.getManagementType() != AssetManagementType.DEVICE) {
            throw new BadRequestAlertException("Chỉ được tạo thiết bị cho item loại quản lý THIẾT BỊ", ENTITY_NAME, "assetitemnotdevice");
        }

        final String serial =
            dto != null && dto.getSerial() != null
                ? dto.getSerial().trim()
                : existing != null
                    ? existing.getSerial()
                    : null;
        if (Boolean.TRUE.equals(item.getSerialTrackingRequired()) && (serial == null || serial.isBlank())) {
            throw new BadRequestAlertException("Thiết bị bắt buộc có serial", ENTITY_NAME, "serialrequired");
        }

        if (Boolean.TRUE.equals(item.getDepreciationEnabled())) {
            final var purchasePrice = dto != null && dto.getPurchasePrice() != null ? dto.getPurchasePrice() : existing != null ? existing.getPurchasePrice() : null;
            final var capitalizationDate =
                dto != null && dto.getCapitalizationDate() != null ? dto.getCapitalizationDate() : existing != null ? existing.getCapitalizationDate() : null;
            final var depreciationMonths =
                dto != null && dto.getDepreciationMonths() != null ? dto.getDepreciationMonths() : existing != null ? existing.getDepreciationMonths() : null;

            if (purchasePrice == null) {
                throw new BadRequestAlertException("Thiết bị khấu hao bắt buộc có nguyên giá", ENTITY_NAME, "purchasepricerequired");
            }
            if (capitalizationDate == null) {
                throw new BadRequestAlertException("Thiết bị khấu hao bắt buộc có ngày vốn hóa", ENTITY_NAME, "capitalizationdaterequired");
            }
            if (depreciationMonths == null) {
                throw new BadRequestAlertException("Thiết bị khấu hao bắt buộc có số tháng khấu hao", ENTITY_NAME, "depreciationmonthsrequired");
            }
        }

        // Optional guard: prevent changing assetItem on patch for in-use equipment
        if (existing != null && dto != null && dto.getAssetItem() != null && dto.getAssetItem().getId() != null) {
            if (!Objects.equals(existing.getAssetItem() != null ? existing.getAssetItem().getId() : null, dto.getAssetItem().getId())) {
                // business can decide; keep strict to avoid accidental data corruption
                throw new BadRequestAlertException("Không cho phép đổi item của thiết bị sau khi tạo", ENTITY_NAME, "assetitemimmutable");
            }
        }
    }
}

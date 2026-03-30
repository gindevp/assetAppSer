package com.gindevp.app.service;

import com.gindevp.app.domain.Equipment;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.mapper.EquipmentMapper;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
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

    private final EquipmentRepository equipmentRepository;

    private final EquipmentMapper equipmentMapper;

    public EquipmentService(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
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
}

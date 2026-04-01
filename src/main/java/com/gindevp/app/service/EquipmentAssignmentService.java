package com.gindevp.app.service;

import com.gindevp.app.domain.EquipmentAssignment;
import com.gindevp.app.repository.EquipmentAssignmentRepository;
import com.gindevp.app.service.dto.EquipmentAssignmentDTO;
import com.gindevp.app.service.mapper.EquipmentAssignmentMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.EquipmentAssignment}.
 */
@Service
@Transactional
public class EquipmentAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentAssignmentService.class);

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    private final EquipmentAssignmentMapper equipmentAssignmentMapper;

    public EquipmentAssignmentService(
        EquipmentAssignmentRepository equipmentAssignmentRepository,
        EquipmentAssignmentMapper equipmentAssignmentMapper
    ) {
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.equipmentAssignmentMapper = equipmentAssignmentMapper;
    }

    /**
     * Save a equipmentAssignment.
     *
     * @param equipmentAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipmentAssignmentDTO save(EquipmentAssignmentDTO equipmentAssignmentDTO) {
        LOG.debug("Request to save EquipmentAssignment : {}", equipmentAssignmentDTO);
        EquipmentAssignment equipmentAssignment = equipmentAssignmentMapper.toEntity(equipmentAssignmentDTO);
        equipmentAssignment = equipmentAssignmentRepository.save(equipmentAssignment);
        return equipmentAssignmentMapper.toDto(equipmentAssignment);
    }

    /**
     * Update a equipmentAssignment.
     *
     * @param equipmentAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public EquipmentAssignmentDTO update(EquipmentAssignmentDTO equipmentAssignmentDTO) {
        LOG.debug("Request to update EquipmentAssignment : {}", equipmentAssignmentDTO);
        EquipmentAssignment equipmentAssignment = equipmentAssignmentMapper.toEntity(equipmentAssignmentDTO);
        equipmentAssignment = equipmentAssignmentRepository.save(equipmentAssignment);
        return equipmentAssignmentMapper.toDto(equipmentAssignment);
    }

    /**
     * Partially update a equipmentAssignment.
     *
     * @param equipmentAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EquipmentAssignmentDTO> partialUpdate(EquipmentAssignmentDTO equipmentAssignmentDTO) {
        LOG.debug("Request to partially update EquipmentAssignment : {}", equipmentAssignmentDTO);

        return equipmentAssignmentRepository
            .findById(equipmentAssignmentDTO.getId())
            .map(existingEquipmentAssignment -> {
                equipmentAssignmentMapper.partialUpdate(existingEquipmentAssignment, equipmentAssignmentDTO);

                return existingEquipmentAssignment;
            })
            .map(equipmentAssignmentRepository::save)
            .map(equipmentAssignmentMapper::toDto);
    }

    /**
     * Get all the equipmentAssignments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EquipmentAssignmentDTO> findAll() {
        LOG.debug("Request to get all EquipmentAssignments");
        return equipmentAssignmentRepository
            .findAllWithToOneRelationships()
            .stream()
            .map(equipmentAssignmentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the equipmentAssignments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<EquipmentAssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return equipmentAssignmentRepository.findAllWithEagerRelationships(pageable).map(equipmentAssignmentMapper::toDto);
    }

    /**
     * Get one equipmentAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EquipmentAssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get EquipmentAssignment : {}", id);
        return equipmentAssignmentRepository.findOneWithEagerRelationships(id).map(equipmentAssignmentMapper::toDto);
    }

    /**
     * Delete the equipmentAssignment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EquipmentAssignment : {}", id);
        equipmentAssignmentRepository.deleteById(id);
    }
}

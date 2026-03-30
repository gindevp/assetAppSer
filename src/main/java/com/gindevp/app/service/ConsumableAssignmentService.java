package com.gindevp.app.service;

import com.gindevp.app.domain.ConsumableAssignment;
import com.gindevp.app.repository.ConsumableAssignmentRepository;
import com.gindevp.app.service.dto.ConsumableAssignmentDTO;
import com.gindevp.app.service.mapper.ConsumableAssignmentMapper;
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
 * Service Implementation for managing {@link com.gindevp.app.domain.ConsumableAssignment}.
 */
@Service
@Transactional
public class ConsumableAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumableAssignmentService.class);

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    private final ConsumableAssignmentMapper consumableAssignmentMapper;

    public ConsumableAssignmentService(
        ConsumableAssignmentRepository consumableAssignmentRepository,
        ConsumableAssignmentMapper consumableAssignmentMapper
    ) {
        this.consumableAssignmentRepository = consumableAssignmentRepository;
        this.consumableAssignmentMapper = consumableAssignmentMapper;
    }

    /**
     * Save a consumableAssignment.
     *
     * @param consumableAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsumableAssignmentDTO save(ConsumableAssignmentDTO consumableAssignmentDTO) {
        LOG.debug("Request to save ConsumableAssignment : {}", consumableAssignmentDTO);
        ConsumableAssignment consumableAssignment = consumableAssignmentMapper.toEntity(consumableAssignmentDTO);
        consumableAssignment = consumableAssignmentRepository.save(consumableAssignment);
        return consumableAssignmentMapper.toDto(consumableAssignment);
    }

    /**
     * Update a consumableAssignment.
     *
     * @param consumableAssignmentDTO the entity to save.
     * @return the persisted entity.
     */
    public ConsumableAssignmentDTO update(ConsumableAssignmentDTO consumableAssignmentDTO) {
        LOG.debug("Request to update ConsumableAssignment : {}", consumableAssignmentDTO);
        ConsumableAssignment consumableAssignment = consumableAssignmentMapper.toEntity(consumableAssignmentDTO);
        consumableAssignment = consumableAssignmentRepository.save(consumableAssignment);
        return consumableAssignmentMapper.toDto(consumableAssignment);
    }

    /**
     * Partially update a consumableAssignment.
     *
     * @param consumableAssignmentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConsumableAssignmentDTO> partialUpdate(ConsumableAssignmentDTO consumableAssignmentDTO) {
        LOG.debug("Request to partially update ConsumableAssignment : {}", consumableAssignmentDTO);

        return consumableAssignmentRepository
            .findById(consumableAssignmentDTO.getId())
            .map(existingConsumableAssignment -> {
                consumableAssignmentMapper.partialUpdate(existingConsumableAssignment, consumableAssignmentDTO);

                return existingConsumableAssignment;
            })
            .map(consumableAssignmentRepository::save)
            .map(consumableAssignmentMapper::toDto);
    }

    /**
     * Get all the consumableAssignments.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ConsumableAssignmentDTO> findAll() {
        LOG.debug("Request to get all ConsumableAssignments");
        return consumableAssignmentRepository
            .findAll()
            .stream()
            .map(consumableAssignmentMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the consumableAssignments with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ConsumableAssignmentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return consumableAssignmentRepository.findAllWithEagerRelationships(pageable).map(consumableAssignmentMapper::toDto);
    }

    /**
     * Get one consumableAssignment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ConsumableAssignmentDTO> findOne(Long id) {
        LOG.debug("Request to get ConsumableAssignment : {}", id);
        return consumableAssignmentRepository.findOneWithEagerRelationships(id).map(consumableAssignmentMapper::toDto);
    }

    /**
     * Delete the consumableAssignment by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ConsumableAssignment : {}", id);
        consumableAssignmentRepository.deleteById(id);
    }
}

package com.gindevp.app.web.rest;

import com.gindevp.app.repository.EquipmentAssignmentRepository;
import com.gindevp.app.service.EquipmentAssignmentService;
import com.gindevp.app.service.dto.EquipmentAssignmentDTO;
import com.gindevp.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.app.domain.EquipmentAssignment}.
 */
@RestController
@RequestMapping("/api/equipment-assignments")
public class EquipmentAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentAssignmentResource.class);

    private static final String ENTITY_NAME = "equipmentAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EquipmentAssignmentService equipmentAssignmentService;

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;

    public EquipmentAssignmentResource(
        EquipmentAssignmentService equipmentAssignmentService,
        EquipmentAssignmentRepository equipmentAssignmentRepository
    ) {
        this.equipmentAssignmentService = equipmentAssignmentService;
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
    }

    /**
     * {@code POST  /equipment-assignments} : Create a new equipmentAssignment.
     *
     * @param equipmentAssignmentDTO the equipmentAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new equipmentAssignmentDTO, or with status {@code 400 (Bad Request)} if the equipmentAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EquipmentAssignmentDTO> createEquipmentAssignment(
        @Valid @RequestBody EquipmentAssignmentDTO equipmentAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save EquipmentAssignment : {}", equipmentAssignmentDTO);
        if (equipmentAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new equipmentAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        equipmentAssignmentDTO = equipmentAssignmentService.save(equipmentAssignmentDTO);
        return ResponseEntity.created(new URI("/api/equipment-assignments/" + equipmentAssignmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, equipmentAssignmentDTO.getId().toString()))
            .body(equipmentAssignmentDTO);
    }

    /**
     * {@code PUT  /equipment-assignments/:id} : Updates an existing equipmentAssignment.
     *
     * @param id the id of the equipmentAssignmentDTO to save.
     * @param equipmentAssignmentDTO the equipmentAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the equipmentAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentAssignmentDTO> updateEquipmentAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EquipmentAssignmentDTO equipmentAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EquipmentAssignment : {}, {}", id, equipmentAssignmentDTO);
        if (equipmentAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipmentAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!equipmentAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        equipmentAssignmentDTO = equipmentAssignmentService.update(equipmentAssignmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentAssignmentDTO.getId().toString()))
            .body(equipmentAssignmentDTO);
    }

    /**
     * {@code PATCH  /equipment-assignments/:id} : Partial updates given fields of an existing equipmentAssignment, field will ignore if it is null
     *
     * @param id the id of the equipmentAssignmentDTO to save.
     * @param equipmentAssignmentDTO the equipmentAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated equipmentAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the equipmentAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the equipmentAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the equipmentAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EquipmentAssignmentDTO> partialUpdateEquipmentAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EquipmentAssignmentDTO equipmentAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EquipmentAssignment partially : {}, {}", id, equipmentAssignmentDTO);
        if (equipmentAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, equipmentAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!equipmentAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EquipmentAssignmentDTO> result = equipmentAssignmentService.partialUpdate(equipmentAssignmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, equipmentAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /equipment-assignments} : get all the equipmentAssignments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of equipmentAssignments in body.
     */
    @GetMapping("")
    public List<EquipmentAssignmentDTO> getAllEquipmentAssignments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all EquipmentAssignments");
        return equipmentAssignmentService.findAll();
    }

    /**
     * {@code GET  /equipment-assignments/:id} : get the "id" equipmentAssignment.
     *
     * @param id the id of the equipmentAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the equipmentAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentAssignmentDTO> getEquipmentAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EquipmentAssignment : {}", id);
        Optional<EquipmentAssignmentDTO> equipmentAssignmentDTO = equipmentAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(equipmentAssignmentDTO);
    }

    /**
     * {@code DELETE  /equipment-assignments/:id} : delete the "id" equipmentAssignment.
     *
     * @param id the id of the equipmentAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipmentAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EquipmentAssignment : {}", id);
        equipmentAssignmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

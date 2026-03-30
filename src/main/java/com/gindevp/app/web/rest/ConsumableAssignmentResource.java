package com.gindevp.app.web.rest;

import com.gindevp.app.repository.ConsumableAssignmentRepository;
import com.gindevp.app.service.ConsumableAssignmentService;
import com.gindevp.app.service.dto.ConsumableAssignmentDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.ConsumableAssignment}.
 */
@RestController
@RequestMapping("/api/consumable-assignments")
public class ConsumableAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumableAssignmentResource.class);

    private static final String ENTITY_NAME = "consumableAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConsumableAssignmentService consumableAssignmentService;

    private final ConsumableAssignmentRepository consumableAssignmentRepository;

    public ConsumableAssignmentResource(
        ConsumableAssignmentService consumableAssignmentService,
        ConsumableAssignmentRepository consumableAssignmentRepository
    ) {
        this.consumableAssignmentService = consumableAssignmentService;
        this.consumableAssignmentRepository = consumableAssignmentRepository;
    }

    /**
     * {@code POST  /consumable-assignments} : Create a new consumableAssignment.
     *
     * @param consumableAssignmentDTO the consumableAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new consumableAssignmentDTO, or with status {@code 400 (Bad Request)} if the consumableAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConsumableAssignmentDTO> createConsumableAssignment(
        @Valid @RequestBody ConsumableAssignmentDTO consumableAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save ConsumableAssignment : {}", consumableAssignmentDTO);
        if (consumableAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new consumableAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        consumableAssignmentDTO = consumableAssignmentService.save(consumableAssignmentDTO);
        return ResponseEntity.created(new URI("/api/consumable-assignments/" + consumableAssignmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, consumableAssignmentDTO.getId().toString()))
            .body(consumableAssignmentDTO);
    }

    /**
     * {@code PUT  /consumable-assignments/:id} : Updates an existing consumableAssignment.
     *
     * @param id the id of the consumableAssignmentDTO to save.
     * @param consumableAssignmentDTO the consumableAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consumableAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the consumableAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the consumableAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConsumableAssignmentDTO> updateConsumableAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ConsumableAssignmentDTO consumableAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ConsumableAssignment : {}, {}", id, consumableAssignmentDTO);
        if (consumableAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consumableAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consumableAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        consumableAssignmentDTO = consumableAssignmentService.update(consumableAssignmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consumableAssignmentDTO.getId().toString()))
            .body(consumableAssignmentDTO);
    }

    /**
     * {@code PATCH  /consumable-assignments/:id} : Partial updates given fields of an existing consumableAssignment, field will ignore if it is null
     *
     * @param id the id of the consumableAssignmentDTO to save.
     * @param consumableAssignmentDTO the consumableAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consumableAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the consumableAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the consumableAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the consumableAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConsumableAssignmentDTO> partialUpdateConsumableAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ConsumableAssignmentDTO consumableAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ConsumableAssignment partially : {}, {}", id, consumableAssignmentDTO);
        if (consumableAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consumableAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consumableAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConsumableAssignmentDTO> result = consumableAssignmentService.partialUpdate(consumableAssignmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consumableAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /consumable-assignments} : get all the consumableAssignments.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of consumableAssignments in body.
     */
    @GetMapping("")
    public List<ConsumableAssignmentDTO> getAllConsumableAssignments(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ConsumableAssignments");
        return consumableAssignmentService.findAll();
    }

    /**
     * {@code GET  /consumable-assignments/:id} : get the "id" consumableAssignment.
     *
     * @param id the id of the consumableAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the consumableAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsumableAssignmentDTO> getConsumableAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ConsumableAssignment : {}", id);
        Optional<ConsumableAssignmentDTO> consumableAssignmentDTO = consumableAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(consumableAssignmentDTO);
    }

    /**
     * {@code DELETE  /consumable-assignments/:id} : delete the "id" consumableAssignment.
     *
     * @param id the id of the consumableAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsumableAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ConsumableAssignment : {}", id);
        consumableAssignmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

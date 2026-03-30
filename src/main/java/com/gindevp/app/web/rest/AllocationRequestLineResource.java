package com.gindevp.app.web.rest;

import com.gindevp.app.repository.AllocationRequestLineRepository;
import com.gindevp.app.service.AllocationRequestLineService;
import com.gindevp.app.service.dto.AllocationRequestLineDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.AllocationRequestLine}.
 */
@RestController
@RequestMapping("/api/allocation-request-lines")
public class AllocationRequestLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(AllocationRequestLineResource.class);

    private static final String ENTITY_NAME = "allocationRequestLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AllocationRequestLineService allocationRequestLineService;

    private final AllocationRequestLineRepository allocationRequestLineRepository;

    public AllocationRequestLineResource(
        AllocationRequestLineService allocationRequestLineService,
        AllocationRequestLineRepository allocationRequestLineRepository
    ) {
        this.allocationRequestLineService = allocationRequestLineService;
        this.allocationRequestLineRepository = allocationRequestLineRepository;
    }

    /**
     * {@code POST  /allocation-request-lines} : Create a new allocationRequestLine.
     *
     * @param allocationRequestLineDTO the allocationRequestLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new allocationRequestLineDTO, or with status {@code 400 (Bad Request)} if the allocationRequestLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AllocationRequestLineDTO> createAllocationRequestLine(
        @Valid @RequestBody AllocationRequestLineDTO allocationRequestLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save AllocationRequestLine : {}", allocationRequestLineDTO);
        if (allocationRequestLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new allocationRequestLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        allocationRequestLineDTO = allocationRequestLineService.save(allocationRequestLineDTO);
        return ResponseEntity.created(new URI("/api/allocation-request-lines/" + allocationRequestLineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, allocationRequestLineDTO.getId().toString()))
            .body(allocationRequestLineDTO);
    }

    /**
     * {@code PUT  /allocation-request-lines/:id} : Updates an existing allocationRequestLine.
     *
     * @param id the id of the allocationRequestLineDTO to save.
     * @param allocationRequestLineDTO the allocationRequestLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated allocationRequestLineDTO,
     * or with status {@code 400 (Bad Request)} if the allocationRequestLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the allocationRequestLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AllocationRequestLineDTO> updateAllocationRequestLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AllocationRequestLineDTO allocationRequestLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AllocationRequestLine : {}, {}", id, allocationRequestLineDTO);
        if (allocationRequestLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, allocationRequestLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!allocationRequestLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        allocationRequestLineDTO = allocationRequestLineService.update(allocationRequestLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, allocationRequestLineDTO.getId().toString()))
            .body(allocationRequestLineDTO);
    }

    /**
     * {@code PATCH  /allocation-request-lines/:id} : Partial updates given fields of an existing allocationRequestLine, field will ignore if it is null
     *
     * @param id the id of the allocationRequestLineDTO to save.
     * @param allocationRequestLineDTO the allocationRequestLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated allocationRequestLineDTO,
     * or with status {@code 400 (Bad Request)} if the allocationRequestLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the allocationRequestLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the allocationRequestLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AllocationRequestLineDTO> partialUpdateAllocationRequestLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AllocationRequestLineDTO allocationRequestLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AllocationRequestLine partially : {}, {}", id, allocationRequestLineDTO);
        if (allocationRequestLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, allocationRequestLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!allocationRequestLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AllocationRequestLineDTO> result = allocationRequestLineService.partialUpdate(allocationRequestLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, allocationRequestLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /allocation-request-lines} : get all the allocationRequestLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of allocationRequestLines in body.
     */
    @GetMapping("")
    public List<AllocationRequestLineDTO> getAllAllocationRequestLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AllocationRequestLines");
        return allocationRequestLineService.findAll();
    }

    /**
     * {@code GET  /allocation-request-lines/:id} : get the "id" allocationRequestLine.
     *
     * @param id the id of the allocationRequestLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the allocationRequestLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AllocationRequestLineDTO> getAllocationRequestLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AllocationRequestLine : {}", id);
        Optional<AllocationRequestLineDTO> allocationRequestLineDTO = allocationRequestLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(allocationRequestLineDTO);
    }

    /**
     * {@code DELETE  /allocation-request-lines/:id} : delete the "id" allocationRequestLine.
     *
     * @param id the id of the allocationRequestLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocationRequestLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AllocationRequestLine : {}", id);
        allocationRequestLineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

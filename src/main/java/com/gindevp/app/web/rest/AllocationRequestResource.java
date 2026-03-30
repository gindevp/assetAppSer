package com.gindevp.app.web.rest;

import com.gindevp.app.repository.AllocationRequestRepository;
import com.gindevp.app.service.AllocationRequestQueryService;
import com.gindevp.app.service.AllocationRequestService;
import com.gindevp.app.service.criteria.AllocationRequestCriteria;
import com.gindevp.app.service.dto.AllocationRequestDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.app.domain.AllocationRequest}.
 */
@RestController
@RequestMapping("/api/allocation-requests")
public class AllocationRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(AllocationRequestResource.class);

    private static final String ENTITY_NAME = "allocationRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AllocationRequestService allocationRequestService;

    private final AllocationRequestRepository allocationRequestRepository;

    private final AllocationRequestQueryService allocationRequestQueryService;

    public AllocationRequestResource(
        AllocationRequestService allocationRequestService,
        AllocationRequestRepository allocationRequestRepository,
        AllocationRequestQueryService allocationRequestQueryService
    ) {
        this.allocationRequestService = allocationRequestService;
        this.allocationRequestRepository = allocationRequestRepository;
        this.allocationRequestQueryService = allocationRequestQueryService;
    }

    /**
     * {@code POST  /allocation-requests} : Create a new allocationRequest.
     *
     * @param allocationRequestDTO the allocationRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new allocationRequestDTO, or with status {@code 400 (Bad Request)} if the allocationRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AllocationRequestDTO> createAllocationRequest(@Valid @RequestBody AllocationRequestDTO allocationRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save AllocationRequest : {}", allocationRequestDTO);
        if (allocationRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new allocationRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        allocationRequestDTO = allocationRequestService.save(allocationRequestDTO);
        return ResponseEntity.created(new URI("/api/allocation-requests/" + allocationRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, allocationRequestDTO.getId().toString()))
            .body(allocationRequestDTO);
    }

    /**
     * {@code PUT  /allocation-requests/:id} : Updates an existing allocationRequest.
     *
     * @param id the id of the allocationRequestDTO to save.
     * @param allocationRequestDTO the allocationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated allocationRequestDTO,
     * or with status {@code 400 (Bad Request)} if the allocationRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the allocationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AllocationRequestDTO> updateAllocationRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AllocationRequestDTO allocationRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AllocationRequest : {}, {}", id, allocationRequestDTO);
        if (allocationRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, allocationRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!allocationRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        allocationRequestDTO = allocationRequestService.update(allocationRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, allocationRequestDTO.getId().toString()))
            .body(allocationRequestDTO);
    }

    /**
     * {@code PATCH  /allocation-requests/:id} : Partial updates given fields of an existing allocationRequest, field will ignore if it is null
     *
     * @param id the id of the allocationRequestDTO to save.
     * @param allocationRequestDTO the allocationRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated allocationRequestDTO,
     * or with status {@code 400 (Bad Request)} if the allocationRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the allocationRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the allocationRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AllocationRequestDTO> partialUpdateAllocationRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AllocationRequestDTO allocationRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AllocationRequest partially : {}, {}", id, allocationRequestDTO);
        if (allocationRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, allocationRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!allocationRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AllocationRequestDTO> result = allocationRequestService.partialUpdate(allocationRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, allocationRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /allocation-requests} : get all the allocationRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of allocationRequests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AllocationRequestDTO>> getAllAllocationRequests(
        AllocationRequestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AllocationRequests by criteria: {}", criteria);

        Page<AllocationRequestDTO> page = allocationRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /allocation-requests/count} : count all the allocationRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAllocationRequests(AllocationRequestCriteria criteria) {
        LOG.debug("REST request to count AllocationRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(allocationRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /allocation-requests/:id} : get the "id" allocationRequest.
     *
     * @param id the id of the allocationRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the allocationRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AllocationRequestDTO> getAllocationRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AllocationRequest : {}", id);
        Optional<AllocationRequestDTO> allocationRequestDTO = allocationRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(allocationRequestDTO);
    }

    /**
     * {@code DELETE  /allocation-requests/:id} : delete the "id" allocationRequest.
     *
     * @param id the id of the allocationRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAllocationRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AllocationRequest : {}", id);
        allocationRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

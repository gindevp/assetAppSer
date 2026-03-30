package com.gindevp.app.web.rest;

import com.gindevp.app.repository.ReturnRequestRepository;
import com.gindevp.app.service.ReturnRequestQueryService;
import com.gindevp.app.service.ReturnRequestService;
import com.gindevp.app.service.criteria.ReturnRequestCriteria;
import com.gindevp.app.service.dto.ReturnRequestDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.ReturnRequest}.
 */
@RestController
@RequestMapping("/api/return-requests")
public class ReturnRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnRequestResource.class);

    private static final String ENTITY_NAME = "returnRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnRequestService returnRequestService;

    private final ReturnRequestRepository returnRequestRepository;

    private final ReturnRequestQueryService returnRequestQueryService;

    public ReturnRequestResource(
        ReturnRequestService returnRequestService,
        ReturnRequestRepository returnRequestRepository,
        ReturnRequestQueryService returnRequestQueryService
    ) {
        this.returnRequestService = returnRequestService;
        this.returnRequestRepository = returnRequestRepository;
        this.returnRequestQueryService = returnRequestQueryService;
    }

    /**
     * {@code POST  /return-requests} : Create a new returnRequest.
     *
     * @param returnRequestDTO the returnRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnRequestDTO, or with status {@code 400 (Bad Request)} if the returnRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnRequestDTO> createReturnRequest(@Valid @RequestBody ReturnRequestDTO returnRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReturnRequest : {}", returnRequestDTO);
        if (returnRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnRequestDTO = returnRequestService.save(returnRequestDTO);
        return ResponseEntity.created(new URI("/api/return-requests/" + returnRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnRequestDTO.getId().toString()))
            .body(returnRequestDTO);
    }

    /**
     * {@code PUT  /return-requests/:id} : Updates an existing returnRequest.
     *
     * @param id the id of the returnRequestDTO to save.
     * @param returnRequestDTO the returnRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnRequestDTO,
     * or with status {@code 400 (Bad Request)} if the returnRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnRequestDTO> updateReturnRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnRequestDTO returnRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnRequest : {}, {}", id, returnRequestDTO);
        if (returnRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnRequestDTO = returnRequestService.update(returnRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnRequestDTO.getId().toString()))
            .body(returnRequestDTO);
    }

    /**
     * {@code PATCH  /return-requests/:id} : Partial updates given fields of an existing returnRequest, field will ignore if it is null
     *
     * @param id the id of the returnRequestDTO to save.
     * @param returnRequestDTO the returnRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnRequestDTO,
     * or with status {@code 400 (Bad Request)} if the returnRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnRequestDTO> partialUpdateReturnRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnRequestDTO returnRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnRequest partially : {}, {}", id, returnRequestDTO);
        if (returnRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnRequestDTO> result = returnRequestService.partialUpdate(returnRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /return-requests} : get all the returnRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnRequests in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ReturnRequestDTO>> getAllReturnRequests(
        ReturnRequestCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get ReturnRequests by criteria: {}", criteria);

        Page<ReturnRequestDTO> page = returnRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /return-requests/count} : count all the returnRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countReturnRequests(ReturnRequestCriteria criteria) {
        LOG.debug("REST request to count ReturnRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(returnRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /return-requests/:id} : get the "id" returnRequest.
     *
     * @param id the id of the returnRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnRequestDTO> getReturnRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnRequest : {}", id);
        Optional<ReturnRequestDTO> returnRequestDTO = returnRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnRequestDTO);
    }

    /**
     * {@code DELETE  /return-requests/:id} : delete the "id" returnRequest.
     *
     * @param id the id of the returnRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnRequest : {}", id);
        returnRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.gindevp.app.web.rest;

import com.gindevp.app.repository.ReturnRequestLineRepository;
import com.gindevp.app.service.ReturnRequestLineService;
import com.gindevp.app.service.dto.ReturnRequestLineDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.ReturnRequestLine}.
 */
@RestController
@RequestMapping("/api/return-request-lines")
public class ReturnRequestLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnRequestLineResource.class);

    private static final String ENTITY_NAME = "returnRequestLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ReturnRequestLineService returnRequestLineService;

    private final ReturnRequestLineRepository returnRequestLineRepository;

    public ReturnRequestLineResource(
        ReturnRequestLineService returnRequestLineService,
        ReturnRequestLineRepository returnRequestLineRepository
    ) {
        this.returnRequestLineService = returnRequestLineService;
        this.returnRequestLineRepository = returnRequestLineRepository;
    }

    /**
     * {@code POST  /return-request-lines} : Create a new returnRequestLine.
     *
     * @param returnRequestLineDTO the returnRequestLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new returnRequestLineDTO, or with status {@code 400 (Bad Request)} if the returnRequestLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ReturnRequestLineDTO> createReturnRequestLine(@Valid @RequestBody ReturnRequestLineDTO returnRequestLineDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ReturnRequestLine : {}", returnRequestLineDTO);
        if (returnRequestLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new returnRequestLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        returnRequestLineDTO = returnRequestLineService.save(returnRequestLineDTO);
        return ResponseEntity.created(new URI("/api/return-request-lines/" + returnRequestLineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, returnRequestLineDTO.getId().toString()))
            .body(returnRequestLineDTO);
    }

    /**
     * {@code PUT  /return-request-lines/:id} : Updates an existing returnRequestLine.
     *
     * @param id the id of the returnRequestLineDTO to save.
     * @param returnRequestLineDTO the returnRequestLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnRequestLineDTO,
     * or with status {@code 400 (Bad Request)} if the returnRequestLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the returnRequestLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReturnRequestLineDTO> updateReturnRequestLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ReturnRequestLineDTO returnRequestLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ReturnRequestLine : {}, {}", id, returnRequestLineDTO);
        if (returnRequestLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnRequestLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnRequestLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        returnRequestLineDTO = returnRequestLineService.update(returnRequestLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnRequestLineDTO.getId().toString()))
            .body(returnRequestLineDTO);
    }

    /**
     * {@code PATCH  /return-request-lines/:id} : Partial updates given fields of an existing returnRequestLine, field will ignore if it is null
     *
     * @param id the id of the returnRequestLineDTO to save.
     * @param returnRequestLineDTO the returnRequestLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated returnRequestLineDTO,
     * or with status {@code 400 (Bad Request)} if the returnRequestLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the returnRequestLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the returnRequestLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ReturnRequestLineDTO> partialUpdateReturnRequestLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ReturnRequestLineDTO returnRequestLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ReturnRequestLine partially : {}, {}", id, returnRequestLineDTO);
        if (returnRequestLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, returnRequestLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!returnRequestLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ReturnRequestLineDTO> result = returnRequestLineService.partialUpdate(returnRequestLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, returnRequestLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /return-request-lines} : get all the returnRequestLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of returnRequestLines in body.
     */
    @GetMapping("")
    public List<ReturnRequestLineDTO> getAllReturnRequestLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ReturnRequestLines");
        return returnRequestLineService.findAll();
    }

    /**
     * {@code GET  /return-request-lines/:id} : get the "id" returnRequestLine.
     *
     * @param id the id of the returnRequestLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the returnRequestLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReturnRequestLineDTO> getReturnRequestLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ReturnRequestLine : {}", id);
        Optional<ReturnRequestLineDTO> returnRequestLineDTO = returnRequestLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(returnRequestLineDTO);
    }

    /**
     * {@code DELETE  /return-request-lines/:id} : delete the "id" returnRequestLine.
     *
     * @param id the id of the returnRequestLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReturnRequestLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ReturnRequestLine : {}", id);
        returnRequestLineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

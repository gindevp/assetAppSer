package com.gindevp.app.web.rest;

import com.gindevp.app.repository.LossReportRequestRepository;
import com.gindevp.app.service.LossReportRequestService;
import com.gindevp.app.service.dto.LossReportRequestDTO;
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

@RestController
@RequestMapping("/api/loss-report-requests")
public class LossReportRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(LossReportRequestResource.class);

    private static final String ENTITY_NAME = "lossReportRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LossReportRequestService lossReportRequestService;

    private final LossReportRequestRepository lossReportRequestRepository;

    public LossReportRequestResource(
        LossReportRequestService lossReportRequestService,
        LossReportRequestRepository lossReportRequestRepository
    ) {
        this.lossReportRequestService = lossReportRequestService;
        this.lossReportRequestRepository = lossReportRequestRepository;
    }

    @PostMapping("")
    public ResponseEntity<LossReportRequestDTO> createLossReportRequest(@Valid @RequestBody LossReportRequestDTO dto)
        throws URISyntaxException {
        LOG.debug("REST request to save LossReportRequest : {}", dto);
        if (dto.getId() != null) {
            throw new BadRequestAlertException("A new loss report cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LossReportRequestDTO result = lossReportRequestService.save(dto);
        return ResponseEntity.created(new URI("/api/loss-report-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LossReportRequestDTO> partialUpdateLossReportRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LossReportRequestDTO dto
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LossReportRequest : {}, {}", id, dto);
        if (dto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, dto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!lossReportRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<LossReportRequestDTO> result = lossReportRequestService.partialUpdate(dto);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, dto.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<LossReportRequestDTO>> getAllLossReportRequests(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get LossReportRequests");
        Page<LossReportRequestDTO> page = lossReportRequestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LossReportRequestDTO> getLossReportRequest(@PathVariable Long id) {
        LOG.debug("REST request to get LossReportRequest : {}", id);
        Optional<LossReportRequestDTO> dto = lossReportRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLossReportRequest(@PathVariable Long id) {
        LOG.debug("REST request to delete LossReportRequest : {}", id);
        lossReportRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

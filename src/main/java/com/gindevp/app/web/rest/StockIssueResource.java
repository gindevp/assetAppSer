package com.gindevp.app.web.rest;

import com.gindevp.app.repository.StockIssueRepository;
import com.gindevp.app.security.AuthoritiesConstants;
import com.gindevp.app.service.DocumentPdfService;
import com.gindevp.app.service.StockDocumentEventService;
import com.gindevp.app.service.StockIssueQueryService;
import com.gindevp.app.service.StockIssueService;
import com.gindevp.app.service.criteria.StockIssueCriteria;
import com.gindevp.app.service.dto.StockDocumentEventDTO;
import com.gindevp.app.service.dto.StockIssueDTO;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.gindevp.app.domain.StockIssue}.
 */
@RestController
@RequestMapping("/api/stock-issues")
public class StockIssueResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockIssueResource.class);

    private static final String ENTITY_NAME = "stockIssue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockIssueService stockIssueService;

    private final StockIssueRepository stockIssueRepository;

    private final StockIssueQueryService stockIssueQueryService;

    private final DocumentPdfService documentPdfService;

    private final StockDocumentEventService stockDocumentEventService;

    public StockIssueResource(
        StockIssueService stockIssueService,
        StockIssueRepository stockIssueRepository,
        StockIssueQueryService stockIssueQueryService,
        DocumentPdfService documentPdfService,
        StockDocumentEventService stockDocumentEventService
    ) {
        this.stockIssueService = stockIssueService;
        this.stockIssueRepository = stockIssueRepository;
        this.stockIssueQueryService = stockIssueQueryService;
        this.documentPdfService = documentPdfService;
        this.stockDocumentEventService = stockDocumentEventService;
    }

    /**
     * {@code POST  /stock-issues} : Create a new stockIssue.
     *
     * @param stockIssueDTO the stockIssueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockIssueDTO, or with status {@code 400 (Bad Request)} if the stockIssue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockIssueDTO> createStockIssue(@Valid @RequestBody StockIssueDTO stockIssueDTO) throws URISyntaxException {
        LOG.debug("REST request to save StockIssue : {}", stockIssueDTO);
        if (stockIssueDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockIssue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockIssueDTO = stockIssueService.save(stockIssueDTO);
        return ResponseEntity.created(new URI("/api/stock-issues/" + stockIssueDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockIssueDTO.getId().toString()))
            .body(stockIssueDTO);
    }

    /**
     * {@code PUT  /stock-issues/:id} : Updates an existing stockIssue.
     *
     * @param id the id of the stockIssueDTO to save.
     * @param stockIssueDTO the stockIssueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockIssueDTO,
     * or with status {@code 400 (Bad Request)} if the stockIssueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockIssueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockIssueDTO> updateStockIssue(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockIssueDTO stockIssueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockIssue : {}, {}", id, stockIssueDTO);
        if (stockIssueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockIssueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockIssueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockIssueDTO = stockIssueService.update(stockIssueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockIssueDTO.getId().toString()))
            .body(stockIssueDTO);
    }

    /**
     * {@code PATCH  /stock-issues/:id} : Partial updates given fields of an existing stockIssue, field will ignore if it is null
     *
     * @param id the id of the stockIssueDTO to save.
     * @param stockIssueDTO the stockIssueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockIssueDTO,
     * or with status {@code 400 (Bad Request)} if the stockIssueDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockIssueDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockIssueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockIssueDTO> partialUpdateStockIssue(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockIssueDTO stockIssueDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockIssue partially : {}, {}", id, stockIssueDTO);
        if (stockIssueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockIssueDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockIssueRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockIssueDTO> result = stockIssueService.partialUpdate(stockIssueDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockIssueDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-issues} : get all the stockIssues.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockIssues in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StockIssueDTO>> getAllStockIssues(
        StockIssueCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StockIssues by criteria: {}", criteria);

        Page<StockIssueDTO> page = stockIssueQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-issues/count} : count all the stockIssues.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStockIssues(StockIssueCriteria criteria) {
        LOG.debug("REST request to count StockIssues by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockIssueQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-issues/:id} : get the "id" stockIssue.
     *
     * @param id the id of the stockIssueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockIssueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockIssueDTO> getStockIssue(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockIssue : {}", id);
        Optional<StockIssueDTO> stockIssueDTO = stockIssueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockIssueDTO);
    }

    /**
     * {@code GET  /stock-issues/:id/events} : lịch sử thao tác trên phiếu xuất.
     */
    @GetMapping("/{id}/events")
    public ResponseEntity<List<StockDocumentEventDTO>> getStockIssueEvents(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockIssue events : {}", id);
        if (!stockIssueRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stockDocumentEventService.findForIssue(id));
    }

    /**
     * {@code GET  /stock-issues/:id/pdf} : tải phiếu xuất PDF (chỉ QLTS / Giám đốc / Admin).
     */
    @GetMapping("/{id}/pdf")
    @PreAuthorize(
        "hasAnyAuthority('" +
        AuthoritiesConstants.ADMIN +
        "', '" +
        AuthoritiesConstants.ASSET_MANAGER +
        "', '" +
        AuthoritiesConstants.GD +
        "')"
    )
    public ResponseEntity<byte[]> getStockIssuePdf(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockIssue PDF : {}", id);
        byte[] pdf = documentPdfService.buildStockIssuePdf(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-issue-" + id + ".pdf")
            .body(pdf);
    }

    /**
     * {@code DELETE  /stock-issues/:id} : delete the "id" stockIssue.
     *
     * @param id the id of the stockIssueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockIssue(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockIssue : {}", id);
        stockIssueService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

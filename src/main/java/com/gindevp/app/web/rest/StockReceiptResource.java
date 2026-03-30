package com.gindevp.app.web.rest;

import com.gindevp.app.repository.StockReceiptRepository;
import com.gindevp.app.security.AuthoritiesConstants;
import com.gindevp.app.service.DocumentPdfService;
import com.gindevp.app.service.StockDocumentEventService;
import com.gindevp.app.service.StockReceiptQueryService;
import com.gindevp.app.service.StockReceiptService;
import com.gindevp.app.service.criteria.StockReceiptCriteria;
import com.gindevp.app.service.dto.StockDocumentEventDTO;
import com.gindevp.app.service.dto.StockReceiptDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.StockReceipt}.
 */
@RestController
@RequestMapping("/api/stock-receipts")
public class StockReceiptResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockReceiptResource.class);

    private static final String ENTITY_NAME = "stockReceipt";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockReceiptService stockReceiptService;

    private final StockReceiptRepository stockReceiptRepository;

    private final StockReceiptQueryService stockReceiptQueryService;

    private final DocumentPdfService documentPdfService;

    private final StockDocumentEventService stockDocumentEventService;

    public StockReceiptResource(
        StockReceiptService stockReceiptService,
        StockReceiptRepository stockReceiptRepository,
        StockReceiptQueryService stockReceiptQueryService,
        DocumentPdfService documentPdfService,
        StockDocumentEventService stockDocumentEventService
    ) {
        this.stockReceiptService = stockReceiptService;
        this.stockReceiptRepository = stockReceiptRepository;
        this.stockReceiptQueryService = stockReceiptQueryService;
        this.documentPdfService = documentPdfService;
        this.stockDocumentEventService = stockDocumentEventService;
    }

    /**
     * {@code POST  /stock-receipts} : Create a new stockReceipt.
     *
     * @param stockReceiptDTO the stockReceiptDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockReceiptDTO, or with status {@code 400 (Bad Request)} if the stockReceipt has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockReceiptDTO> createStockReceipt(@Valid @RequestBody StockReceiptDTO stockReceiptDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StockReceipt : {}", stockReceiptDTO);
        if (stockReceiptDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockReceipt cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockReceiptDTO = stockReceiptService.save(stockReceiptDTO);
        return ResponseEntity.created(new URI("/api/stock-receipts/" + stockReceiptDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockReceiptDTO.getId().toString()))
            .body(stockReceiptDTO);
    }

    /**
     * {@code PUT  /stock-receipts/:id} : Updates an existing stockReceipt.
     *
     * @param id the id of the stockReceiptDTO to save.
     * @param stockReceiptDTO the stockReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the stockReceiptDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockReceiptDTO> updateStockReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockReceiptDTO stockReceiptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockReceipt : {}, {}", id, stockReceiptDTO);
        if (stockReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockReceiptDTO = stockReceiptService.update(stockReceiptDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockReceiptDTO.getId().toString()))
            .body(stockReceiptDTO);
    }

    /**
     * {@code PATCH  /stock-receipts/:id} : Partial updates given fields of an existing stockReceipt, field will ignore if it is null
     *
     * @param id the id of the stockReceiptDTO to save.
     * @param stockReceiptDTO the stockReceiptDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockReceiptDTO,
     * or with status {@code 400 (Bad Request)} if the stockReceiptDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockReceiptDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockReceiptDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockReceiptDTO> partialUpdateStockReceipt(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockReceiptDTO stockReceiptDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockReceipt partially : {}, {}", id, stockReceiptDTO);
        if (stockReceiptDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockReceiptDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockReceiptRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockReceiptDTO> result = stockReceiptService.partialUpdate(stockReceiptDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockReceiptDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-receipts} : get all the stockReceipts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockReceipts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<StockReceiptDTO>> getAllStockReceipts(
        StockReceiptCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get StockReceipts by criteria: {}", criteria);

        Page<StockReceiptDTO> page = stockReceiptQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /stock-receipts/count} : count all the stockReceipts.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStockReceipts(StockReceiptCriteria criteria) {
        LOG.debug("REST request to count StockReceipts by criteria: {}", criteria);
        return ResponseEntity.ok().body(stockReceiptQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /stock-receipts/:id} : get the "id" stockReceipt.
     *
     * @param id the id of the stockReceiptDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockReceiptDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockReceiptDTO> getStockReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockReceipt : {}", id);
        Optional<StockReceiptDTO> stockReceiptDTO = stockReceiptService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockReceiptDTO);
    }

    /**
     * {@code GET  /stock-receipts/:id/events} : lịch sử thao tác trên phiếu nhập.
     */
    @GetMapping("/{id}/events")
    public ResponseEntity<List<StockDocumentEventDTO>> getStockReceiptEvents(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockReceipt events : {}", id);
        if (!stockReceiptRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stockDocumentEventService.findForReceipt(id));
    }

    /**
     * {@code GET  /stock-receipts/:id/pdf} : tải phiếu nhập PDF (chỉ QLTS / Giám đốc / Admin).
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
    public ResponseEntity<byte[]> getStockReceiptPdf(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockReceipt PDF : {}", id);
        byte[] pdf = documentPdfService.buildStockReceiptPdf(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=stock-receipt-" + id + ".pdf")
            .body(pdf);
    }

    /**
     * {@code DELETE  /stock-receipts/:id} : delete the "id" stockReceipt.
     *
     * @param id the id of the stockReceiptDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockReceipt(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockReceipt : {}", id);
        stockReceiptService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

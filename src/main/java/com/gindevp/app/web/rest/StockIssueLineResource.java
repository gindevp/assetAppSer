package com.gindevp.app.web.rest;

import com.gindevp.app.repository.StockIssueLineRepository;
import com.gindevp.app.service.StockIssueLineService;
import com.gindevp.app.service.dto.StockIssueLineDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.StockIssueLine}.
 */
@RestController
@RequestMapping("/api/stock-issue-lines")
public class StockIssueLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockIssueLineResource.class);

    private static final String ENTITY_NAME = "stockIssueLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockIssueLineService stockIssueLineService;

    private final StockIssueLineRepository stockIssueLineRepository;

    public StockIssueLineResource(StockIssueLineService stockIssueLineService, StockIssueLineRepository stockIssueLineRepository) {
        this.stockIssueLineService = stockIssueLineService;
        this.stockIssueLineRepository = stockIssueLineRepository;
    }

    /**
     * {@code POST  /stock-issue-lines} : Create a new stockIssueLine.
     *
     * @param stockIssueLineDTO the stockIssueLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockIssueLineDTO, or with status {@code 400 (Bad Request)} if the stockIssueLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockIssueLineDTO> createStockIssueLine(@Valid @RequestBody StockIssueLineDTO stockIssueLineDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StockIssueLine : {}", stockIssueLineDTO);
        if (stockIssueLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockIssueLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockIssueLineDTO = stockIssueLineService.save(stockIssueLineDTO);
        return ResponseEntity.created(new URI("/api/stock-issue-lines/" + stockIssueLineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockIssueLineDTO.getId().toString()))
            .body(stockIssueLineDTO);
    }

    /**
     * {@code PUT  /stock-issue-lines/:id} : Updates an existing stockIssueLine.
     *
     * @param id the id of the stockIssueLineDTO to save.
     * @param stockIssueLineDTO the stockIssueLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockIssueLineDTO,
     * or with status {@code 400 (Bad Request)} if the stockIssueLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockIssueLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockIssueLineDTO> updateStockIssueLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockIssueLineDTO stockIssueLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockIssueLine : {}, {}", id, stockIssueLineDTO);
        if (stockIssueLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockIssueLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockIssueLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockIssueLineDTO = stockIssueLineService.update(stockIssueLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockIssueLineDTO.getId().toString()))
            .body(stockIssueLineDTO);
    }

    /**
     * {@code PATCH  /stock-issue-lines/:id} : Partial updates given fields of an existing stockIssueLine, field will ignore if it is null
     *
     * @param id the id of the stockIssueLineDTO to save.
     * @param stockIssueLineDTO the stockIssueLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockIssueLineDTO,
     * or with status {@code 400 (Bad Request)} if the stockIssueLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockIssueLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockIssueLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockIssueLineDTO> partialUpdateStockIssueLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockIssueLineDTO stockIssueLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockIssueLine partially : {}, {}", id, stockIssueLineDTO);
        if (stockIssueLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockIssueLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockIssueLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockIssueLineDTO> result = stockIssueLineService.partialUpdate(stockIssueLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockIssueLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-issue-lines} : get all the stockIssueLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockIssueLines in body.
     */
    @GetMapping("")
    public List<StockIssueLineDTO> getAllStockIssueLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all StockIssueLines");
        return stockIssueLineService.findAll();
    }

    /**
     * {@code GET  /stock-issue-lines/:id} : get the "id" stockIssueLine.
     *
     * @param id the id of the stockIssueLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockIssueLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockIssueLineDTO> getStockIssueLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockIssueLine : {}", id);
        Optional<StockIssueLineDTO> stockIssueLineDTO = stockIssueLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockIssueLineDTO);
    }

    /**
     * {@code DELETE  /stock-issue-lines/:id} : delete the "id" stockIssueLine.
     *
     * @param id the id of the stockIssueLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockIssueLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockIssueLine : {}", id);
        stockIssueLineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

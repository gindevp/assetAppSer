package com.gindevp.app.web.rest;

import com.gindevp.app.repository.StockReceiptLineRepository;
import com.gindevp.app.service.StockReceiptLineService;
import com.gindevp.app.service.dto.StockReceiptLineDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.StockReceiptLine}.
 */
@RestController
@RequestMapping("/api/stock-receipt-lines")
public class StockReceiptLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(StockReceiptLineResource.class);

    private static final String ENTITY_NAME = "stockReceiptLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StockReceiptLineService stockReceiptLineService;

    private final StockReceiptLineRepository stockReceiptLineRepository;

    public StockReceiptLineResource(
        StockReceiptLineService stockReceiptLineService,
        StockReceiptLineRepository stockReceiptLineRepository
    ) {
        this.stockReceiptLineService = stockReceiptLineService;
        this.stockReceiptLineRepository = stockReceiptLineRepository;
    }

    /**
     * {@code POST  /stock-receipt-lines} : Create a new stockReceiptLine.
     *
     * @param stockReceiptLineDTO the stockReceiptLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new stockReceiptLineDTO, or with status {@code 400 (Bad Request)} if the stockReceiptLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<StockReceiptLineDTO> createStockReceiptLine(@Valid @RequestBody StockReceiptLineDTO stockReceiptLineDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save StockReceiptLine : {}", stockReceiptLineDTO);
        if (stockReceiptLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new stockReceiptLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        stockReceiptLineDTO = stockReceiptLineService.save(stockReceiptLineDTO);
        return ResponseEntity.created(new URI("/api/stock-receipt-lines/" + stockReceiptLineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, stockReceiptLineDTO.getId().toString()))
            .body(stockReceiptLineDTO);
    }

    /**
     * {@code PUT  /stock-receipt-lines/:id} : Updates an existing stockReceiptLine.
     *
     * @param id the id of the stockReceiptLineDTO to save.
     * @param stockReceiptLineDTO the stockReceiptLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockReceiptLineDTO,
     * or with status {@code 400 (Bad Request)} if the stockReceiptLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the stockReceiptLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<StockReceiptLineDTO> updateStockReceiptLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody StockReceiptLineDTO stockReceiptLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update StockReceiptLine : {}, {}", id, stockReceiptLineDTO);
        if (stockReceiptLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockReceiptLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockReceiptLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        stockReceiptLineDTO = stockReceiptLineService.update(stockReceiptLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockReceiptLineDTO.getId().toString()))
            .body(stockReceiptLineDTO);
    }

    /**
     * {@code PATCH  /stock-receipt-lines/:id} : Partial updates given fields of an existing stockReceiptLine, field will ignore if it is null
     *
     * @param id the id of the stockReceiptLineDTO to save.
     * @param stockReceiptLineDTO the stockReceiptLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated stockReceiptLineDTO,
     * or with status {@code 400 (Bad Request)} if the stockReceiptLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the stockReceiptLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the stockReceiptLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<StockReceiptLineDTO> partialUpdateStockReceiptLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody StockReceiptLineDTO stockReceiptLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update StockReceiptLine partially : {}, {}", id, stockReceiptLineDTO);
        if (stockReceiptLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, stockReceiptLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!stockReceiptLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<StockReceiptLineDTO> result = stockReceiptLineService.partialUpdate(stockReceiptLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, stockReceiptLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /stock-receipt-lines} : get all the stockReceiptLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of stockReceiptLines in body.
     */
    @GetMapping("")
    public List<StockReceiptLineDTO> getAllStockReceiptLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all StockReceiptLines");
        return stockReceiptLineService.findAll();
    }

    /**
     * {@code GET  /stock-receipt-lines/:id} : get the "id" stockReceiptLine.
     *
     * @param id the id of the stockReceiptLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the stockReceiptLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StockReceiptLineDTO> getStockReceiptLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get StockReceiptLine : {}", id);
        Optional<StockReceiptLineDTO> stockReceiptLineDTO = stockReceiptLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(stockReceiptLineDTO);
    }

    /**
     * {@code DELETE  /stock-receipt-lines/:id} : delete the "id" stockReceiptLine.
     *
     * @param id the id of the stockReceiptLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStockReceiptLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete StockReceiptLine : {}", id);
        stockReceiptLineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.gindevp.app.web.rest;

import com.gindevp.app.repository.ConsumableStockRepository;
import com.gindevp.app.service.ConsumableStockService;
import com.gindevp.app.service.dto.ConsumableStockDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.ConsumableStock}.
 */
@RestController
@RequestMapping("/api/consumable-stocks")
public class ConsumableStockResource {

    private static final Logger LOG = LoggerFactory.getLogger(ConsumableStockResource.class);

    private static final String ENTITY_NAME = "consumableStock";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConsumableStockService consumableStockService;

    private final ConsumableStockRepository consumableStockRepository;

    public ConsumableStockResource(ConsumableStockService consumableStockService, ConsumableStockRepository consumableStockRepository) {
        this.consumableStockService = consumableStockService;
        this.consumableStockRepository = consumableStockRepository;
    }

    /**
     * {@code POST  /consumable-stocks} : Create a new consumableStock.
     *
     * @param consumableStockDTO the consumableStockDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new consumableStockDTO, or with status {@code 400 (Bad Request)} if the consumableStock has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ConsumableStockDTO> createConsumableStock(@Valid @RequestBody ConsumableStockDTO consumableStockDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ConsumableStock : {}", consumableStockDTO);
        if (consumableStockDTO.getId() != null) {
            throw new BadRequestAlertException("A new consumableStock cannot already have an ID", ENTITY_NAME, "idexists");
        }
        consumableStockDTO = consumableStockService.save(consumableStockDTO);
        return ResponseEntity.created(new URI("/api/consumable-stocks/" + consumableStockDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, consumableStockDTO.getId().toString()))
            .body(consumableStockDTO);
    }

    /**
     * {@code PUT  /consumable-stocks/:id} : Updates an existing consumableStock.
     *
     * @param id the id of the consumableStockDTO to save.
     * @param consumableStockDTO the consumableStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consumableStockDTO,
     * or with status {@code 400 (Bad Request)} if the consumableStockDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the consumableStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ConsumableStockDTO> updateConsumableStock(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ConsumableStockDTO consumableStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ConsumableStock : {}, {}", id, consumableStockDTO);
        if (consumableStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consumableStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consumableStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        consumableStockDTO = consumableStockService.update(consumableStockDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consumableStockDTO.getId().toString()))
            .body(consumableStockDTO);
    }

    /**
     * {@code PATCH  /consumable-stocks/:id} : Partial updates given fields of an existing consumableStock, field will ignore if it is null
     *
     * @param id the id of the consumableStockDTO to save.
     * @param consumableStockDTO the consumableStockDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated consumableStockDTO,
     * or with status {@code 400 (Bad Request)} if the consumableStockDTO is not valid,
     * or with status {@code 404 (Not Found)} if the consumableStockDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the consumableStockDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConsumableStockDTO> partialUpdateConsumableStock(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ConsumableStockDTO consumableStockDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ConsumableStock partially : {}, {}", id, consumableStockDTO);
        if (consumableStockDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, consumableStockDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!consumableStockRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConsumableStockDTO> result = consumableStockService.partialUpdate(consumableStockDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, consumableStockDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /consumable-stocks} : get all the consumableStocks.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of consumableStocks in body.
     */
    @GetMapping("")
    public List<ConsumableStockDTO> getAllConsumableStocks(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all ConsumableStocks");
        return consumableStockService.findAll();
    }

    /**
     * {@code GET  /consumable-stocks/:id} : get the "id" consumableStock.
     *
     * @param id the id of the consumableStockDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the consumableStockDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ConsumableStockDTO> getConsumableStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ConsumableStock : {}", id);
        Optional<ConsumableStockDTO> consumableStockDTO = consumableStockService.findOne(id);
        return ResponseUtil.wrapOrNotFound(consumableStockDTO);
    }

    /**
     * {@code DELETE  /consumable-stocks/:id} : delete the "id" consumableStock.
     *
     * @param id the id of the consumableStockDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsumableStock(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ConsumableStock : {}", id);
        consumableStockService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

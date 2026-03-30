package com.gindevp.app.web.rest;

import com.gindevp.app.repository.AssetLineRepository;
import com.gindevp.app.service.AssetLineService;
import com.gindevp.app.service.dto.AssetLineDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.AssetLine}.
 */
@RestController
@RequestMapping("/api/asset-lines")
public class AssetLineResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetLineResource.class);

    private static final String ENTITY_NAME = "assetLine";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssetLineService assetLineService;

    private final AssetLineRepository assetLineRepository;

    public AssetLineResource(AssetLineService assetLineService, AssetLineRepository assetLineRepository) {
        this.assetLineService = assetLineService;
        this.assetLineRepository = assetLineRepository;
    }

    /**
     * {@code POST  /asset-lines} : Create a new assetLine.
     *
     * @param assetLineDTO the assetLineDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assetLineDTO, or with status {@code 400 (Bad Request)} if the assetLine has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssetLineDTO> createAssetLine(@Valid @RequestBody AssetLineDTO assetLineDTO) throws URISyntaxException {
        LOG.debug("REST request to save AssetLine : {}", assetLineDTO);
        if (assetLineDTO.getId() != null) {
            throw new BadRequestAlertException("A new assetLine cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assetLineDTO = assetLineService.save(assetLineDTO);
        return ResponseEntity.created(new URI("/api/asset-lines/" + assetLineDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assetLineDTO.getId().toString()))
            .body(assetLineDTO);
    }

    /**
     * {@code PUT  /asset-lines/:id} : Updates an existing assetLine.
     *
     * @param id the id of the assetLineDTO to save.
     * @param assetLineDTO the assetLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetLineDTO,
     * or with status {@code 400 (Bad Request)} if the assetLineDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assetLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetLineDTO> updateAssetLine(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssetLineDTO assetLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssetLine : {}, {}", id, assetLineDTO);
        if (assetLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assetLineDTO = assetLineService.update(assetLineDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetLineDTO.getId().toString()))
            .body(assetLineDTO);
    }

    /**
     * {@code PATCH  /asset-lines/:id} : Partial updates given fields of an existing assetLine, field will ignore if it is null
     *
     * @param id the id of the assetLineDTO to save.
     * @param assetLineDTO the assetLineDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetLineDTO,
     * or with status {@code 400 (Bad Request)} if the assetLineDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assetLineDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assetLineDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssetLineDTO> partialUpdateAssetLine(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssetLineDTO assetLineDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssetLine partially : {}, {}", id, assetLineDTO);
        if (assetLineDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetLineDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetLineRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssetLineDTO> result = assetLineService.partialUpdate(assetLineDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetLineDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /asset-lines} : get all the assetLines.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assetLines in body.
     */
    @GetMapping("")
    public List<AssetLineDTO> getAllAssetLines(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AssetLines");
        return assetLineService.findAll();
    }

    /**
     * {@code GET  /asset-lines/:id} : get the "id" assetLine.
     *
     * @param id the id of the assetLineDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assetLineDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetLineDTO> getAssetLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssetLine : {}", id);
        Optional<AssetLineDTO> assetLineDTO = assetLineService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assetLineDTO);
    }

    /**
     * {@code DELETE  /asset-lines/:id} : delete the "id" assetLine.
     *
     * @param id the id of the assetLineDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssetLine(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssetLine : {}", id);
        assetLineService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

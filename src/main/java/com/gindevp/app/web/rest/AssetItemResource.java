package com.gindevp.app.web.rest;

import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.service.AssetItemQueryService;
import com.gindevp.app.service.AssetItemService;
import com.gindevp.app.service.criteria.AssetItemCriteria;
import com.gindevp.app.service.dto.AssetItemDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.AssetItem}.
 */
@RestController
@RequestMapping("/api/asset-items")
public class AssetItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetItemResource.class);

    private static final String ENTITY_NAME = "assetItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssetItemService assetItemService;

    private final AssetItemRepository assetItemRepository;

    private final AssetItemQueryService assetItemQueryService;

    public AssetItemResource(
        AssetItemService assetItemService,
        AssetItemRepository assetItemRepository,
        AssetItemQueryService assetItemQueryService
    ) {
        this.assetItemService = assetItemService;
        this.assetItemRepository = assetItemRepository;
        this.assetItemQueryService = assetItemQueryService;
    }

    /**
     * {@code POST  /asset-items} : Create a new assetItem.
     *
     * @param assetItemDTO the assetItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assetItemDTO, or with status {@code 400 (Bad Request)} if the assetItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssetItemDTO> createAssetItem(@Valid @RequestBody AssetItemDTO assetItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save AssetItem : {}", assetItemDTO);
        if (assetItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new assetItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assetItemDTO = assetItemService.save(assetItemDTO);
        return ResponseEntity.created(new URI("/api/asset-items/" + assetItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assetItemDTO.getId().toString()))
            .body(assetItemDTO);
    }

    /**
     * {@code PUT  /asset-items/:id} : Updates an existing assetItem.
     *
     * @param id the id of the assetItemDTO to save.
     * @param assetItemDTO the assetItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetItemDTO,
     * or with status {@code 400 (Bad Request)} if the assetItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assetItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetItemDTO> updateAssetItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssetItemDTO assetItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssetItem : {}, {}", id, assetItemDTO);
        if (assetItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assetItemDTO = assetItemService.update(assetItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetItemDTO.getId().toString()))
            .body(assetItemDTO);
    }

    /**
     * {@code PATCH  /asset-items/:id} : Partial updates given fields of an existing assetItem, field will ignore if it is null
     *
     * @param id the id of the assetItemDTO to save.
     * @param assetItemDTO the assetItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetItemDTO,
     * or with status {@code 400 (Bad Request)} if the assetItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assetItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assetItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssetItemDTO> partialUpdateAssetItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssetItemDTO assetItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssetItem partially : {}, {}", id, assetItemDTO);
        if (assetItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssetItemDTO> result = assetItemService.partialUpdate(assetItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /asset-items} : get all the assetItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assetItems in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AssetItemDTO>> getAllAssetItems(
        AssetItemCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AssetItems by criteria: {}", criteria);

        Page<AssetItemDTO> page = assetItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /asset-items/count} : count all the assetItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countAssetItems(AssetItemCriteria criteria) {
        LOG.debug("REST request to count AssetItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(assetItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /asset-items/:id} : get the "id" assetItem.
     *
     * @param id the id of the assetItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assetItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetItemDTO> getAssetItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssetItem : {}", id);
        Optional<AssetItemDTO> assetItemDTO = assetItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assetItemDTO);
    }

    /**
     * {@code DELETE  /asset-items/:id} : delete the "id" assetItem.
     *
     * @param id the id of the assetItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssetItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssetItem : {}", id);
        assetItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.gindevp.app.web.rest;

import com.gindevp.app.repository.AssetTypeRepository;
import com.gindevp.app.service.AssetTypeService;
import com.gindevp.app.service.dto.AssetTypeDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.AssetType}.
 */
@RestController
@RequestMapping("/api/asset-types")
public class AssetTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetTypeResource.class);

    private static final String ENTITY_NAME = "assetType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssetTypeService assetTypeService;

    private final AssetTypeRepository assetTypeRepository;

    public AssetTypeResource(AssetTypeService assetTypeService, AssetTypeRepository assetTypeRepository) {
        this.assetTypeService = assetTypeService;
        this.assetTypeRepository = assetTypeRepository;
    }

    /**
     * {@code POST  /asset-types} : Create a new assetType.
     *
     * @param assetTypeDTO the assetTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assetTypeDTO, or with status {@code 400 (Bad Request)} if the assetType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssetTypeDTO> createAssetType(@Valid @RequestBody AssetTypeDTO assetTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save AssetType : {}", assetTypeDTO);
        if (assetTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new assetType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assetTypeDTO = assetTypeService.save(assetTypeDTO);
        return ResponseEntity.created(new URI("/api/asset-types/" + assetTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assetTypeDTO.getId().toString()))
            .body(assetTypeDTO);
    }

    /**
     * {@code PUT  /asset-types/:id} : Updates an existing assetType.
     *
     * @param id the id of the assetTypeDTO to save.
     * @param assetTypeDTO the assetTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetTypeDTO,
     * or with status {@code 400 (Bad Request)} if the assetTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assetTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetTypeDTO> updateAssetType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssetTypeDTO assetTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssetType : {}, {}", id, assetTypeDTO);
        if (assetTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assetTypeDTO = assetTypeService.update(assetTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetTypeDTO.getId().toString()))
            .body(assetTypeDTO);
    }

    /**
     * {@code PATCH  /asset-types/:id} : Partial updates given fields of an existing assetType, field will ignore if it is null
     *
     * @param id the id of the assetTypeDTO to save.
     * @param assetTypeDTO the assetTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetTypeDTO,
     * or with status {@code 400 (Bad Request)} if the assetTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assetTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assetTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssetTypeDTO> partialUpdateAssetType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssetTypeDTO assetTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssetType partially : {}, {}", id, assetTypeDTO);
        if (assetTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssetTypeDTO> result = assetTypeService.partialUpdate(assetTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /asset-types} : get all the assetTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assetTypes in body.
     */
    @GetMapping("")
    public List<AssetTypeDTO> getAllAssetTypes() {
        LOG.debug("REST request to get all AssetTypes");
        return assetTypeService.findAll();
    }

    /**
     * {@code GET  /asset-types/:id} : get the "id" assetType.
     *
     * @param id the id of the assetTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assetTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetTypeDTO> getAssetType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssetType : {}", id);
        Optional<AssetTypeDTO> assetTypeDTO = assetTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assetTypeDTO);
    }

    /**
     * {@code DELETE  /asset-types/:id} : delete the "id" assetType.
     *
     * @param id the id of the assetTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssetType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssetType : {}", id);
        assetTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

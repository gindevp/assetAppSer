package com.gindevp.app.web.rest;

import com.gindevp.app.repository.AssetGroupRepository;
import com.gindevp.app.service.AssetGroupService;
import com.gindevp.app.service.dto.AssetGroupDTO;
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
 * REST controller for managing {@link com.gindevp.app.domain.AssetGroup}.
 */
@RestController
@RequestMapping("/api/asset-groups")
public class AssetGroupResource {

    private static final Logger LOG = LoggerFactory.getLogger(AssetGroupResource.class);

    private static final String ENTITY_NAME = "assetGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AssetGroupService assetGroupService;

    private final AssetGroupRepository assetGroupRepository;

    public AssetGroupResource(AssetGroupService assetGroupService, AssetGroupRepository assetGroupRepository) {
        this.assetGroupService = assetGroupService;
        this.assetGroupRepository = assetGroupRepository;
    }

    /**
     * {@code POST  /asset-groups} : Create a new assetGroup.
     *
     * @param assetGroupDTO the assetGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new assetGroupDTO, or with status {@code 400 (Bad Request)} if the assetGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AssetGroupDTO> createAssetGroup(@Valid @RequestBody AssetGroupDTO assetGroupDTO) throws URISyntaxException {
        LOG.debug("REST request to save AssetGroup : {}", assetGroupDTO);
        if (assetGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new assetGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        assetGroupDTO = assetGroupService.save(assetGroupDTO);
        return ResponseEntity.created(new URI("/api/asset-groups/" + assetGroupDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, assetGroupDTO.getId().toString()))
            .body(assetGroupDTO);
    }

    /**
     * {@code PUT  /asset-groups/:id} : Updates an existing assetGroup.
     *
     * @param id the id of the assetGroupDTO to save.
     * @param assetGroupDTO the assetGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetGroupDTO,
     * or with status {@code 400 (Bad Request)} if the assetGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the assetGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AssetGroupDTO> updateAssetGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AssetGroupDTO assetGroupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AssetGroup : {}, {}", id, assetGroupDTO);
        if (assetGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        assetGroupDTO = assetGroupService.update(assetGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetGroupDTO.getId().toString()))
            .body(assetGroupDTO);
    }

    /**
     * {@code PATCH  /asset-groups/:id} : Partial updates given fields of an existing assetGroup, field will ignore if it is null
     *
     * @param id the id of the assetGroupDTO to save.
     * @param assetGroupDTO the assetGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated assetGroupDTO,
     * or with status {@code 400 (Bad Request)} if the assetGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the assetGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the assetGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AssetGroupDTO> partialUpdateAssetGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AssetGroupDTO assetGroupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AssetGroup partially : {}, {}", id, assetGroupDTO);
        if (assetGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, assetGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!assetGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AssetGroupDTO> result = assetGroupService.partialUpdate(assetGroupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, assetGroupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /asset-groups} : get all the assetGroups.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of assetGroups in body.
     */
    @GetMapping("")
    public List<AssetGroupDTO> getAllAssetGroups(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all AssetGroups");
        return assetGroupService.findAll();
    }

    /**
     * {@code GET  /asset-groups/:id} : get the "id" assetGroup.
     *
     * @param id the id of the assetGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the assetGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssetGroupDTO> getAssetGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AssetGroup : {}", id);
        Optional<AssetGroupDTO> assetGroupDTO = assetGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(assetGroupDTO);
    }

    /**
     * {@code DELETE  /asset-groups/:id} : delete the "id" assetGroup.
     *
     * @param id the id of the assetGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssetGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AssetGroup : {}", id);
        assetGroupService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

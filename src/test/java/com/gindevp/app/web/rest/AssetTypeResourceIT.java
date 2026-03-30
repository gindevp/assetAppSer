package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.AssetTypeAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AssetType;
import com.gindevp.app.repository.AssetTypeRepository;
import com.gindevp.app.service.dto.AssetTypeDTO;
import com.gindevp.app.service.mapper.AssetTypeMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AssetTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AssetTypeResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/asset-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetTypeRepository assetTypeRepository;

    @Autowired
    private AssetTypeMapper assetTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssetTypeMockMvc;

    private AssetType assetType;

    private AssetType insertedAssetType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetType createEntity() {
        return new AssetType().code(DEFAULT_CODE).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetType createUpdatedEntity() {
        return new AssetType().code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        assetType = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssetType != null) {
            assetTypeRepository.delete(insertedAssetType);
            insertedAssetType = null;
        }
    }

    @Test
    @Transactional
    void createAssetType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);
        var returnedAssetTypeDTO = om.readValue(
            restAssetTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssetTypeDTO.class
        );

        // Validate the AssetType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssetType = assetTypeMapper.toEntity(returnedAssetTypeDTO);
        assertAssetTypeUpdatableFieldsEquals(returnedAssetType, getPersistedAssetType(returnedAssetType));

        insertedAssetType = returnedAssetType;
    }

    @Test
    @Transactional
    void createAssetTypeWithExistingId() throws Exception {
        // Create the AssetType with an existing ID
        assetType.setId(1L);
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetType.setCode(null);

        // Create the AssetType, which fails.
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        restAssetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetType.setName(null);

        // Create the AssetType, which fails.
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        restAssetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetType.setActive(null);

        // Create the AssetType, which fails.
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        restAssetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssetTypes() throws Exception {
        // Initialize the database
        insertedAssetType = assetTypeRepository.saveAndFlush(assetType);

        // Get all the assetTypeList
        restAssetTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetType.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getAssetType() throws Exception {
        // Initialize the database
        insertedAssetType = assetTypeRepository.saveAndFlush(assetType);

        // Get the assetType
        restAssetTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, assetType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assetType.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingAssetType() throws Exception {
        // Get the assetType
        restAssetTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssetType() throws Exception {
        // Initialize the database
        insertedAssetType = assetTypeRepository.saveAndFlush(assetType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetType
        AssetType updatedAssetType = assetTypeRepository.findById(assetType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssetType are not directly saved in db
        em.detach(updatedAssetType);
        updatedAssetType.code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(updatedAssetType);

        restAssetTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssetTypeToMatchAllProperties(updatedAssetType);
    }

    @Test
    @Transactional
    void putNonExistingAssetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetType.setId(longCount.incrementAndGet());

        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetType.setId(longCount.incrementAndGet());

        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetType.setId(longCount.incrementAndGet());

        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssetTypeWithPatch() throws Exception {
        // Initialize the database
        insertedAssetType = assetTypeRepository.saveAndFlush(assetType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetType using partial update
        AssetType partialUpdatedAssetType = new AssetType();
        partialUpdatedAssetType.setId(assetType.getId());

        partialUpdatedAssetType.code(UPDATED_CODE).name(UPDATED_NAME);

        restAssetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetType))
            )
            .andExpect(status().isOk());

        // Validate the AssetType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssetType, assetType),
            getPersistedAssetType(assetType)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssetTypeWithPatch() throws Exception {
        // Initialize the database
        insertedAssetType = assetTypeRepository.saveAndFlush(assetType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetType using partial update
        AssetType partialUpdatedAssetType = new AssetType();
        partialUpdatedAssetType.setId(assetType.getId());

        partialUpdatedAssetType.code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);

        restAssetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetType))
            )
            .andExpect(status().isOk());

        // Validate the AssetType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetTypeUpdatableFieldsEquals(partialUpdatedAssetType, getPersistedAssetType(partialUpdatedAssetType));
    }

    @Test
    @Transactional
    void patchNonExistingAssetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetType.setId(longCount.incrementAndGet());

        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assetTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetType.setId(longCount.incrementAndGet());

        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetType.setId(longCount.incrementAndGet());

        // Create the AssetType
        AssetTypeDTO assetTypeDTO = assetTypeMapper.toDto(assetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assetTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssetType() throws Exception {
        // Initialize the database
        insertedAssetType = assetTypeRepository.saveAndFlush(assetType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assetType
        restAssetTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, assetType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assetTypeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AssetType getPersistedAssetType(AssetType assetType) {
        return assetTypeRepository.findById(assetType.getId()).orElseThrow();
    }

    protected void assertPersistedAssetTypeToMatchAllProperties(AssetType expectedAssetType) {
        assertAssetTypeAllPropertiesEquals(expectedAssetType, getPersistedAssetType(expectedAssetType));
    }

    protected void assertPersistedAssetTypeToMatchUpdatableProperties(AssetType expectedAssetType) {
        assertAssetTypeAllUpdatablePropertiesEquals(expectedAssetType, getPersistedAssetType(expectedAssetType));
    }
}

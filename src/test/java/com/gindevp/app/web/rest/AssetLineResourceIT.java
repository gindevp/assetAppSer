package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.AssetLineAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.repository.AssetLineRepository;
import com.gindevp.app.service.AssetLineService;
import com.gindevp.app.service.dto.AssetLineDTO;
import com.gindevp.app.service.mapper.AssetLineMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AssetLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssetLineResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/asset-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetLineRepository assetLineRepository;

    @Mock
    private AssetLineRepository assetLineRepositoryMock;

    @Autowired
    private AssetLineMapper assetLineMapper;

    @Mock
    private AssetLineService assetLineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssetLineMockMvc;

    private AssetLine assetLine;

    private AssetLine insertedAssetLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetLine createEntity() {
        return new AssetLine().code(DEFAULT_CODE).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetLine createUpdatedEntity() {
        return new AssetLine().code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        assetLine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssetLine != null) {
            assetLineRepository.delete(insertedAssetLine);
            insertedAssetLine = null;
        }
    }

    @Test
    @Transactional
    void createAssetLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);
        var returnedAssetLineDTO = om.readValue(
            restAssetLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetLineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssetLineDTO.class
        );

        // Validate the AssetLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssetLine = assetLineMapper.toEntity(returnedAssetLineDTO);
        assertAssetLineUpdatableFieldsEquals(returnedAssetLine, getPersistedAssetLine(returnedAssetLine));

        insertedAssetLine = returnedAssetLine;
    }

    @Test
    @Transactional
    void createAssetLineWithExistingId() throws Exception {
        // Create the AssetLine with an existing ID
        assetLine.setId(1L);
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetLine.setCode(null);

        // Create the AssetLine, which fails.
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        restAssetLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetLine.setName(null);

        // Create the AssetLine, which fails.
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        restAssetLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetLine.setActive(null);

        // Create the AssetLine, which fails.
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        restAssetLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssetLines() throws Exception {
        // Initialize the database
        insertedAssetLine = assetLineRepository.saveAndFlush(assetLine);

        // Get all the assetLineList
        restAssetLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssetLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(assetLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssetLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assetLineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssetLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assetLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssetLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assetLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssetLine() throws Exception {
        // Initialize the database
        insertedAssetLine = assetLineRepository.saveAndFlush(assetLine);

        // Get the assetLine
        restAssetLineMockMvc
            .perform(get(ENTITY_API_URL_ID, assetLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assetLine.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingAssetLine() throws Exception {
        // Get the assetLine
        restAssetLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssetLine() throws Exception {
        // Initialize the database
        insertedAssetLine = assetLineRepository.saveAndFlush(assetLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetLine
        AssetLine updatedAssetLine = assetLineRepository.findById(assetLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssetLine are not directly saved in db
        em.detach(updatedAssetLine);
        updatedAssetLine.code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(updatedAssetLine);

        restAssetLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssetLineToMatchAllProperties(updatedAssetLine);
    }

    @Test
    @Transactional
    void putNonExistingAssetLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetLine.setId(longCount.incrementAndGet());

        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssetLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetLine.setId(longCount.incrementAndGet());

        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssetLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetLine.setId(longCount.incrementAndGet());

        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssetLineWithPatch() throws Exception {
        // Initialize the database
        insertedAssetLine = assetLineRepository.saveAndFlush(assetLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetLine using partial update
        AssetLine partialUpdatedAssetLine = new AssetLine();
        partialUpdatedAssetLine.setId(assetLine.getId());

        partialUpdatedAssetLine.active(UPDATED_ACTIVE);

        restAssetLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetLine))
            )
            .andExpect(status().isOk());

        // Validate the AssetLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssetLine, assetLine),
            getPersistedAssetLine(assetLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssetLineWithPatch() throws Exception {
        // Initialize the database
        insertedAssetLine = assetLineRepository.saveAndFlush(assetLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetLine using partial update
        AssetLine partialUpdatedAssetLine = new AssetLine();
        partialUpdatedAssetLine.setId(assetLine.getId());

        partialUpdatedAssetLine.code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);

        restAssetLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetLine))
            )
            .andExpect(status().isOk());

        // Validate the AssetLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetLineUpdatableFieldsEquals(partialUpdatedAssetLine, getPersistedAssetLine(partialUpdatedAssetLine));
    }

    @Test
    @Transactional
    void patchNonExistingAssetLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetLine.setId(longCount.incrementAndGet());

        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assetLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssetLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetLine.setId(longCount.incrementAndGet());

        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssetLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetLine.setId(longCount.incrementAndGet());

        // Create the AssetLine
        AssetLineDTO assetLineDTO = assetLineMapper.toDto(assetLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assetLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssetLine() throws Exception {
        // Initialize the database
        insertedAssetLine = assetLineRepository.saveAndFlush(assetLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assetLine
        restAssetLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, assetLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assetLineRepository.count();
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

    protected AssetLine getPersistedAssetLine(AssetLine assetLine) {
        return assetLineRepository.findById(assetLine.getId()).orElseThrow();
    }

    protected void assertPersistedAssetLineToMatchAllProperties(AssetLine expectedAssetLine) {
        assertAssetLineAllPropertiesEquals(expectedAssetLine, getPersistedAssetLine(expectedAssetLine));
    }

    protected void assertPersistedAssetLineToMatchUpdatableProperties(AssetLine expectedAssetLine) {
        assertAssetLineAllUpdatablePropertiesEquals(expectedAssetLine, getPersistedAssetLine(expectedAssetLine));
    }
}

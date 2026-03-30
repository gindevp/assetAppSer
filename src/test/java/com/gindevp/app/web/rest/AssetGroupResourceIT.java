package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.AssetGroupAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AssetGroup;
import com.gindevp.app.repository.AssetGroupRepository;
import com.gindevp.app.service.AssetGroupService;
import com.gindevp.app.service.dto.AssetGroupDTO;
import com.gindevp.app.service.mapper.AssetGroupMapper;
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
 * Integration tests for the {@link AssetGroupResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssetGroupResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/asset-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetGroupRepository assetGroupRepository;

    @Mock
    private AssetGroupRepository assetGroupRepositoryMock;

    @Autowired
    private AssetGroupMapper assetGroupMapper;

    @Mock
    private AssetGroupService assetGroupServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssetGroupMockMvc;

    private AssetGroup assetGroup;

    private AssetGroup insertedAssetGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetGroup createEntity() {
        return new AssetGroup().code(DEFAULT_CODE).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetGroup createUpdatedEntity() {
        return new AssetGroup().code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        assetGroup = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssetGroup != null) {
            assetGroupRepository.delete(insertedAssetGroup);
            insertedAssetGroup = null;
        }
    }

    @Test
    @Transactional
    void createAssetGroup() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);
        var returnedAssetGroupDTO = om.readValue(
            restAssetGroupMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetGroupDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssetGroupDTO.class
        );

        // Validate the AssetGroup in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssetGroup = assetGroupMapper.toEntity(returnedAssetGroupDTO);
        assertAssetGroupUpdatableFieldsEquals(returnedAssetGroup, getPersistedAssetGroup(returnedAssetGroup));

        insertedAssetGroup = returnedAssetGroup;
    }

    @Test
    @Transactional
    void createAssetGroupWithExistingId() throws Exception {
        // Create the AssetGroup with an existing ID
        assetGroup.setId(1L);
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetGroup.setCode(null);

        // Create the AssetGroup, which fails.
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        restAssetGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetGroup.setName(null);

        // Create the AssetGroup, which fails.
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        restAssetGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetGroup.setActive(null);

        // Create the AssetGroup, which fails.
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        restAssetGroupMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssetGroups() throws Exception {
        // Initialize the database
        insertedAssetGroup = assetGroupRepository.saveAndFlush(assetGroup);

        // Get all the assetGroupList
        restAssetGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssetGroupsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assetGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssetGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assetGroupServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssetGroupsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assetGroupServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssetGroupMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assetGroupRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssetGroup() throws Exception {
        // Initialize the database
        insertedAssetGroup = assetGroupRepository.saveAndFlush(assetGroup);

        // Get the assetGroup
        restAssetGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, assetGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assetGroup.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingAssetGroup() throws Exception {
        // Get the assetGroup
        restAssetGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssetGroup() throws Exception {
        // Initialize the database
        insertedAssetGroup = assetGroupRepository.saveAndFlush(assetGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetGroup
        AssetGroup updatedAssetGroup = assetGroupRepository.findById(assetGroup.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssetGroup are not directly saved in db
        em.detach(updatedAssetGroup);
        updatedAssetGroup.code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(updatedAssetGroup);

        restAssetGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssetGroupToMatchAllProperties(updatedAssetGroup);
    }

    @Test
    @Transactional
    void putNonExistingAssetGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetGroup.setId(longCount.incrementAndGet());

        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetGroupDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssetGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetGroup.setId(longCount.incrementAndGet());

        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssetGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetGroup.setId(longCount.incrementAndGet());

        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetGroupMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetGroupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssetGroupWithPatch() throws Exception {
        // Initialize the database
        insertedAssetGroup = assetGroupRepository.saveAndFlush(assetGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetGroup using partial update
        AssetGroup partialUpdatedAssetGroup = new AssetGroup();
        partialUpdatedAssetGroup.setId(assetGroup.getId());

        partialUpdatedAssetGroup.code(UPDATED_CODE);

        restAssetGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetGroup))
            )
            .andExpect(status().isOk());

        // Validate the AssetGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetGroupUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssetGroup, assetGroup),
            getPersistedAssetGroup(assetGroup)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssetGroupWithPatch() throws Exception {
        // Initialize the database
        insertedAssetGroup = assetGroupRepository.saveAndFlush(assetGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetGroup using partial update
        AssetGroup partialUpdatedAssetGroup = new AssetGroup();
        partialUpdatedAssetGroup.setId(assetGroup.getId());

        partialUpdatedAssetGroup.code(UPDATED_CODE).name(UPDATED_NAME).description(UPDATED_DESCRIPTION).active(UPDATED_ACTIVE);

        restAssetGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetGroup.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetGroup))
            )
            .andExpect(status().isOk());

        // Validate the AssetGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetGroupUpdatableFieldsEquals(partialUpdatedAssetGroup, getPersistedAssetGroup(partialUpdatedAssetGroup));
    }

    @Test
    @Transactional
    void patchNonExistingAssetGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetGroup.setId(longCount.incrementAndGet());

        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assetGroupDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssetGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetGroup.setId(longCount.incrementAndGet());

        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssetGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetGroup.setId(longCount.incrementAndGet());

        // Create the AssetGroup
        AssetGroupDTO assetGroupDTO = assetGroupMapper.toDto(assetGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetGroupMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assetGroupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssetGroup() throws Exception {
        // Initialize the database
        insertedAssetGroup = assetGroupRepository.saveAndFlush(assetGroup);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assetGroup
        restAssetGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, assetGroup.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assetGroupRepository.count();
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

    protected AssetGroup getPersistedAssetGroup(AssetGroup assetGroup) {
        return assetGroupRepository.findById(assetGroup.getId()).orElseThrow();
    }

    protected void assertPersistedAssetGroupToMatchAllProperties(AssetGroup expectedAssetGroup) {
        assertAssetGroupAllPropertiesEquals(expectedAssetGroup, getPersistedAssetGroup(expectedAssetGroup));
    }

    protected void assertPersistedAssetGroupToMatchUpdatableProperties(AssetGroup expectedAssetGroup) {
        assertAssetGroupAllUpdatablePropertiesEquals(expectedAssetGroup, getPersistedAssetGroup(expectedAssetGroup));
    }
}

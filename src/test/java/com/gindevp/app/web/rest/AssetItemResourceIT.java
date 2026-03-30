package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.AssetItemAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.service.AssetItemService;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.mapper.AssetItemMapper;
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
 * Integration tests for the {@link AssetItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AssetItemResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final AssetManagementType DEFAULT_MANAGEMENT_TYPE = AssetManagementType.DEVICE;
    private static final AssetManagementType UPDATED_MANAGEMENT_TYPE = AssetManagementType.CONSUMABLE;

    private static final String DEFAULT_UNIT = "AAAAAAAAAA";
    private static final String UPDATED_UNIT = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DEPRECIATION_ENABLED = false;
    private static final Boolean UPDATED_DEPRECIATION_ENABLED = true;

    private static final Boolean DEFAULT_SERIAL_TRACKING_REQUIRED = false;
    private static final Boolean UPDATED_SERIAL_TRACKING_REQUIRED = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/asset-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AssetItemRepository assetItemRepository;

    @Mock
    private AssetItemRepository assetItemRepositoryMock;

    @Autowired
    private AssetItemMapper assetItemMapper;

    @Mock
    private AssetItemService assetItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAssetItemMockMvc;

    private AssetItem assetItem;

    private AssetItem insertedAssetItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetItem createEntity() {
        return new AssetItem()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME)
            .managementType(DEFAULT_MANAGEMENT_TYPE)
            .unit(DEFAULT_UNIT)
            .depreciationEnabled(DEFAULT_DEPRECIATION_ENABLED)
            .serialTrackingRequired(DEFAULT_SERIAL_TRACKING_REQUIRED)
            .note(DEFAULT_NOTE)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AssetItem createUpdatedEntity() {
        return new AssetItem()
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .managementType(UPDATED_MANAGEMENT_TYPE)
            .unit(UPDATED_UNIT)
            .depreciationEnabled(UPDATED_DEPRECIATION_ENABLED)
            .serialTrackingRequired(UPDATED_SERIAL_TRACKING_REQUIRED)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    public void initTest() {
        assetItem = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAssetItem != null) {
            assetItemRepository.delete(insertedAssetItem);
            insertedAssetItem = null;
        }
    }

    @Test
    @Transactional
    void createAssetItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);
        var returnedAssetItemDTO = om.readValue(
            restAssetItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AssetItemDTO.class
        );

        // Validate the AssetItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAssetItem = assetItemMapper.toEntity(returnedAssetItemDTO);
        assertAssetItemUpdatableFieldsEquals(returnedAssetItem, getPersistedAssetItem(returnedAssetItem));

        insertedAssetItem = returnedAssetItem;
    }

    @Test
    @Transactional
    void createAssetItemWithExistingId() throws Exception {
        // Create the AssetItem with an existing ID
        assetItem.setId(1L);
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetItem.setCode(null);

        // Create the AssetItem, which fails.
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetItem.setName(null);

        // Create the AssetItem, which fails.
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkManagementTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetItem.setManagementType(null);

        // Create the AssetItem, which fails.
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDepreciationEnabledIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetItem.setDepreciationEnabled(null);

        // Create the AssetItem, which fails.
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSerialTrackingRequiredIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetItem.setSerialTrackingRequired(null);

        // Create the AssetItem, which fails.
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        assetItem.setActive(null);

        // Create the AssetItem, which fails.
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        restAssetItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAssetItems() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList
        restAssetItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].managementType").value(hasItem(DEFAULT_MANAGEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].depreciationEnabled").value(hasItem(DEFAULT_DEPRECIATION_ENABLED)))
            .andExpect(jsonPath("$.[*].serialTrackingRequired").value(hasItem(DEFAULT_SERIAL_TRACKING_REQUIRED)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssetItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(assetItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssetItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(assetItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAssetItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(assetItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAssetItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(assetItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAssetItem() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get the assetItem
        restAssetItemMockMvc
            .perform(get(ENTITY_API_URL_ID, assetItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(assetItem.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.managementType").value(DEFAULT_MANAGEMENT_TYPE.toString()))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT))
            .andExpect(jsonPath("$.depreciationEnabled").value(DEFAULT_DEPRECIATION_ENABLED))
            .andExpect(jsonPath("$.serialTrackingRequired").value(DEFAULT_SERIAL_TRACKING_REQUIRED))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getAssetItemsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        Long id = assetItem.getId();

        defaultAssetItemFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAssetItemFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAssetItemFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAssetItemsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where code equals to
        defaultAssetItemFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where code in
        defaultAssetItemFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where code is not null
        defaultAssetItemFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where code contains
        defaultAssetItemFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where code does not contain
        defaultAssetItemFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where name equals to
        defaultAssetItemFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where name in
        defaultAssetItemFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where name is not null
        defaultAssetItemFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where name contains
        defaultAssetItemFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where name does not contain
        defaultAssetItemFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllAssetItemsByManagementTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where managementType equals to
        defaultAssetItemFiltering("managementType.equals=" + DEFAULT_MANAGEMENT_TYPE, "managementType.equals=" + UPDATED_MANAGEMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByManagementTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where managementType in
        defaultAssetItemFiltering(
            "managementType.in=" + DEFAULT_MANAGEMENT_TYPE + "," + UPDATED_MANAGEMENT_TYPE,
            "managementType.in=" + UPDATED_MANAGEMENT_TYPE
        );
    }

    @Test
    @Transactional
    void getAllAssetItemsByManagementTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where managementType is not null
        defaultAssetItemFiltering("managementType.specified=true", "managementType.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByUnitIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where unit equals to
        defaultAssetItemFiltering("unit.equals=" + DEFAULT_UNIT, "unit.equals=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllAssetItemsByUnitIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where unit in
        defaultAssetItemFiltering("unit.in=" + DEFAULT_UNIT + "," + UPDATED_UNIT, "unit.in=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllAssetItemsByUnitIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where unit is not null
        defaultAssetItemFiltering("unit.specified=true", "unit.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByUnitContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where unit contains
        defaultAssetItemFiltering("unit.contains=" + DEFAULT_UNIT, "unit.contains=" + UPDATED_UNIT);
    }

    @Test
    @Transactional
    void getAllAssetItemsByUnitNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where unit does not contain
        defaultAssetItemFiltering("unit.doesNotContain=" + UPDATED_UNIT, "unit.doesNotContain=" + DEFAULT_UNIT);
    }

    @Test
    @Transactional
    void getAllAssetItemsByDepreciationEnabledIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where depreciationEnabled equals to
        defaultAssetItemFiltering(
            "depreciationEnabled.equals=" + DEFAULT_DEPRECIATION_ENABLED,
            "depreciationEnabled.equals=" + UPDATED_DEPRECIATION_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllAssetItemsByDepreciationEnabledIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where depreciationEnabled in
        defaultAssetItemFiltering(
            "depreciationEnabled.in=" + DEFAULT_DEPRECIATION_ENABLED + "," + UPDATED_DEPRECIATION_ENABLED,
            "depreciationEnabled.in=" + UPDATED_DEPRECIATION_ENABLED
        );
    }

    @Test
    @Transactional
    void getAllAssetItemsByDepreciationEnabledIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where depreciationEnabled is not null
        defaultAssetItemFiltering("depreciationEnabled.specified=true", "depreciationEnabled.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsBySerialTrackingRequiredIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where serialTrackingRequired equals to
        defaultAssetItemFiltering(
            "serialTrackingRequired.equals=" + DEFAULT_SERIAL_TRACKING_REQUIRED,
            "serialTrackingRequired.equals=" + UPDATED_SERIAL_TRACKING_REQUIRED
        );
    }

    @Test
    @Transactional
    void getAllAssetItemsBySerialTrackingRequiredIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where serialTrackingRequired in
        defaultAssetItemFiltering(
            "serialTrackingRequired.in=" + DEFAULT_SERIAL_TRACKING_REQUIRED + "," + UPDATED_SERIAL_TRACKING_REQUIRED,
            "serialTrackingRequired.in=" + UPDATED_SERIAL_TRACKING_REQUIRED
        );
    }

    @Test
    @Transactional
    void getAllAssetItemsBySerialTrackingRequiredIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where serialTrackingRequired is not null
        defaultAssetItemFiltering("serialTrackingRequired.specified=true", "serialTrackingRequired.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where note equals to
        defaultAssetItemFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where note in
        defaultAssetItemFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where note is not null
        defaultAssetItemFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where note contains
        defaultAssetItemFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where note does not contain
        defaultAssetItemFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where active equals to
        defaultAssetItemFiltering("active.equals=" + DEFAULT_ACTIVE, "active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where active in
        defaultAssetItemFiltering("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE, "active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAssetItemsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        // Get all the assetItemList where active is not null
        defaultAssetItemFiltering("active.specified=true", "active.specified=false");
    }

    @Test
    @Transactional
    void getAllAssetItemsByAssetLineIsEqualToSomething() throws Exception {
        AssetLine assetLine;
        if (TestUtil.findAll(em, AssetLine.class).isEmpty()) {
            assetItemRepository.saveAndFlush(assetItem);
            assetLine = AssetLineResourceIT.createEntity();
        } else {
            assetLine = TestUtil.findAll(em, AssetLine.class).get(0);
        }
        em.persist(assetLine);
        em.flush();
        assetItem.setAssetLine(assetLine);
        assetItemRepository.saveAndFlush(assetItem);
        Long assetLineId = assetLine.getId();
        // Get all the assetItemList where assetLine equals to assetLineId
        defaultAssetItemShouldBeFound("assetLineId.equals=" + assetLineId);

        // Get all the assetItemList where assetLine equals to (assetLineId + 1)
        defaultAssetItemShouldNotBeFound("assetLineId.equals=" + (assetLineId + 1));
    }

    private void defaultAssetItemFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAssetItemShouldBeFound(shouldBeFound);
        defaultAssetItemShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAssetItemShouldBeFound(String filter) throws Exception {
        restAssetItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(assetItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].managementType").value(hasItem(DEFAULT_MANAGEMENT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT)))
            .andExpect(jsonPath("$.[*].depreciationEnabled").value(hasItem(DEFAULT_DEPRECIATION_ENABLED)))
            .andExpect(jsonPath("$.[*].serialTrackingRequired").value(hasItem(DEFAULT_SERIAL_TRACKING_REQUIRED)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));

        // Check, that the count call also returns 1
        restAssetItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAssetItemShouldNotBeFound(String filter) throws Exception {
        restAssetItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAssetItemMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAssetItem() throws Exception {
        // Get the assetItem
        restAssetItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAssetItem() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetItem
        AssetItem updatedAssetItem = assetItemRepository.findById(assetItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAssetItem are not directly saved in db
        em.detach(updatedAssetItem);
        updatedAssetItem
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .managementType(UPDATED_MANAGEMENT_TYPE)
            .unit(UPDATED_UNIT)
            .depreciationEnabled(UPDATED_DEPRECIATION_ENABLED)
            .serialTrackingRequired(UPDATED_SERIAL_TRACKING_REQUIRED)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(updatedAssetItem);

        restAssetItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAssetItemToMatchAllProperties(updatedAssetItem);
    }

    @Test
    @Transactional
    void putNonExistingAssetItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetItem.setId(longCount.incrementAndGet());

        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, assetItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAssetItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetItem.setId(longCount.incrementAndGet());

        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(assetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAssetItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetItem.setId(longCount.incrementAndGet());

        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAssetItemWithPatch() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetItem using partial update
        AssetItem partialUpdatedAssetItem = new AssetItem();
        partialUpdatedAssetItem.setId(assetItem.getId());

        partialUpdatedAssetItem
            .managementType(UPDATED_MANAGEMENT_TYPE)
            .depreciationEnabled(UPDATED_DEPRECIATION_ENABLED)
            .serialTrackingRequired(UPDATED_SERIAL_TRACKING_REQUIRED)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);

        restAssetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetItem))
            )
            .andExpect(status().isOk());

        // Validate the AssetItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAssetItem, assetItem),
            getPersistedAssetItem(assetItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateAssetItemWithPatch() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the assetItem using partial update
        AssetItem partialUpdatedAssetItem = new AssetItem();
        partialUpdatedAssetItem.setId(assetItem.getId());

        partialUpdatedAssetItem
            .code(UPDATED_CODE)
            .name(UPDATED_NAME)
            .managementType(UPDATED_MANAGEMENT_TYPE)
            .unit(UPDATED_UNIT)
            .depreciationEnabled(UPDATED_DEPRECIATION_ENABLED)
            .serialTrackingRequired(UPDATED_SERIAL_TRACKING_REQUIRED)
            .note(UPDATED_NOTE)
            .active(UPDATED_ACTIVE);

        restAssetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAssetItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAssetItem))
            )
            .andExpect(status().isOk());

        // Validate the AssetItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAssetItemUpdatableFieldsEquals(partialUpdatedAssetItem, getPersistedAssetItem(partialUpdatedAssetItem));
    }

    @Test
    @Transactional
    void patchNonExistingAssetItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetItem.setId(longCount.incrementAndGet());

        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAssetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, assetItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAssetItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetItem.setId(longCount.incrementAndGet());

        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(assetItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAssetItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        assetItem.setId(longCount.incrementAndGet());

        // Create the AssetItem
        AssetItemDTO assetItemDTO = assetItemMapper.toDto(assetItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAssetItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(assetItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AssetItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAssetItem() throws Exception {
        // Initialize the database
        insertedAssetItem = assetItemRepository.saveAndFlush(assetItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the assetItem
        restAssetItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, assetItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return assetItemRepository.count();
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

    protected AssetItem getPersistedAssetItem(AssetItem assetItem) {
        return assetItemRepository.findById(assetItem.getId()).orElseThrow();
    }

    protected void assertPersistedAssetItemToMatchAllProperties(AssetItem expectedAssetItem) {
        assertAssetItemAllPropertiesEquals(expectedAssetItem, getPersistedAssetItem(expectedAssetItem));
    }

    protected void assertPersistedAssetItemToMatchUpdatableProperties(AssetItem expectedAssetItem) {
        assertAssetItemAllUpdatablePropertiesEquals(expectedAssetItem, getPersistedAssetItem(expectedAssetItem));
    }
}

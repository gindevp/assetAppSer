package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.RepairRequestAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import com.gindevp.app.repository.RepairRequestRepository;
import com.gindevp.app.service.RepairRequestService;
import com.gindevp.app.service.dto.RepairRequestDTO;
import com.gindevp.app.service.mapper.RepairRequestMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link RepairRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RepairRequestResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PROBLEM_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_PROBLEM_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final RepairRequestStatus DEFAULT_STATUS = RepairRequestStatus.NEW;
    private static final RepairRequestStatus UPDATED_STATUS = RepairRequestStatus.ACCEPTED;

    private static final String DEFAULT_RESOLUTION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_RESOLUTION_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/repair-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RepairRequestRepository repairRequestRepository;

    @Mock
    private RepairRequestRepository repairRequestRepositoryMock;

    @Autowired
    private RepairRequestMapper repairRequestMapper;

    @Mock
    private RepairRequestService repairRequestServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRepairRequestMockMvc;

    private RepairRequest repairRequest;

    private RepairRequest insertedRepairRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RepairRequest createEntity() {
        return new RepairRequest()
            .code(DEFAULT_CODE)
            .requestDate(DEFAULT_REQUEST_DATE)
            .problemCategory(DEFAULT_PROBLEM_CATEGORY)
            .description(DEFAULT_DESCRIPTION)
            .status(DEFAULT_STATUS)
            .resolutionNote(DEFAULT_RESOLUTION_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RepairRequest createUpdatedEntity() {
        return new RepairRequest()
            .code(UPDATED_CODE)
            .requestDate(UPDATED_REQUEST_DATE)
            .problemCategory(UPDATED_PROBLEM_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);
    }

    @BeforeEach
    public void initTest() {
        repairRequest = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRepairRequest != null) {
            repairRequestRepository.delete(insertedRepairRequest);
            insertedRepairRequest = null;
        }
    }

    @Test
    @Transactional
    void createRepairRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);
        var returnedRepairRequestDTO = om.readValue(
            restRepairRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(repairRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RepairRequestDTO.class
        );

        // Validate the RepairRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRepairRequest = repairRequestMapper.toEntity(returnedRepairRequestDTO);
        assertRepairRequestUpdatableFieldsEquals(returnedRepairRequest, getPersistedRepairRequest(returnedRepairRequest));

        insertedRepairRequest = returnedRepairRequest;
    }

    @Test
    @Transactional
    void createRepairRequestWithExistingId() throws Exception {
        // Create the RepairRequest with an existing ID
        repairRequest.setId(1L);
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRepairRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(repairRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        repairRequest.setCode(null);

        // Create the RepairRequest, which fails.
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        restRepairRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(repairRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequestDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        repairRequest.setRequestDate(null);

        // Create the RepairRequest, which fails.
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        restRepairRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(repairRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        repairRequest.setStatus(null);

        // Create the RepairRequest, which fails.
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        restRepairRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(repairRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRepairRequests() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList
        restRepairRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(repairRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].problemCategory").value(hasItem(DEFAULT_PROBLEM_CATEGORY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].resolutionNote").value(hasItem(DEFAULT_RESOLUTION_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRepairRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(repairRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRepairRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(repairRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRepairRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(repairRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRepairRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(repairRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getRepairRequest() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get the repairRequest
        restRepairRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, repairRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(repairRequest.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.requestDate").value(DEFAULT_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.problemCategory").value(DEFAULT_PROBLEM_CATEGORY))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.resolutionNote").value(DEFAULT_RESOLUTION_NOTE));
    }

    @Test
    @Transactional
    void getRepairRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        Long id = repairRequest.getId();

        defaultRepairRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultRepairRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultRepairRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where code equals to
        defaultRepairRequestFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where code in
        defaultRepairRequestFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where code is not null
        defaultRepairRequestFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllRepairRequestsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where code contains
        defaultRepairRequestFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where code does not contain
        defaultRepairRequestFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByRequestDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where requestDate equals to
        defaultRepairRequestFiltering("requestDate.equals=" + DEFAULT_REQUEST_DATE, "requestDate.equals=" + UPDATED_REQUEST_DATE);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByRequestDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where requestDate in
        defaultRepairRequestFiltering(
            "requestDate.in=" + DEFAULT_REQUEST_DATE + "," + UPDATED_REQUEST_DATE,
            "requestDate.in=" + UPDATED_REQUEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByRequestDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where requestDate is not null
        defaultRepairRequestFiltering("requestDate.specified=true", "requestDate.specified=false");
    }

    @Test
    @Transactional
    void getAllRepairRequestsByProblemCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where problemCategory equals to
        defaultRepairRequestFiltering(
            "problemCategory.equals=" + DEFAULT_PROBLEM_CATEGORY,
            "problemCategory.equals=" + UPDATED_PROBLEM_CATEGORY
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByProblemCategoryIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where problemCategory in
        defaultRepairRequestFiltering(
            "problemCategory.in=" + DEFAULT_PROBLEM_CATEGORY + "," + UPDATED_PROBLEM_CATEGORY,
            "problemCategory.in=" + UPDATED_PROBLEM_CATEGORY
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByProblemCategoryIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where problemCategory is not null
        defaultRepairRequestFiltering("problemCategory.specified=true", "problemCategory.specified=false");
    }

    @Test
    @Transactional
    void getAllRepairRequestsByProblemCategoryContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where problemCategory contains
        defaultRepairRequestFiltering(
            "problemCategory.contains=" + DEFAULT_PROBLEM_CATEGORY,
            "problemCategory.contains=" + UPDATED_PROBLEM_CATEGORY
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByProblemCategoryNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where problemCategory does not contain
        defaultRepairRequestFiltering(
            "problemCategory.doesNotContain=" + UPDATED_PROBLEM_CATEGORY,
            "problemCategory.doesNotContain=" + DEFAULT_PROBLEM_CATEGORY
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where description equals to
        defaultRepairRequestFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where description in
        defaultRepairRequestFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where description is not null
        defaultRepairRequestFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllRepairRequestsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where description contains
        defaultRepairRequestFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where description does not contain
        defaultRepairRequestFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where status equals to
        defaultRepairRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where status in
        defaultRepairRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllRepairRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where status is not null
        defaultRepairRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllRepairRequestsByResolutionNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where resolutionNote equals to
        defaultRepairRequestFiltering(
            "resolutionNote.equals=" + DEFAULT_RESOLUTION_NOTE,
            "resolutionNote.equals=" + UPDATED_RESOLUTION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByResolutionNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where resolutionNote in
        defaultRepairRequestFiltering(
            "resolutionNote.in=" + DEFAULT_RESOLUTION_NOTE + "," + UPDATED_RESOLUTION_NOTE,
            "resolutionNote.in=" + UPDATED_RESOLUTION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByResolutionNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where resolutionNote is not null
        defaultRepairRequestFiltering("resolutionNote.specified=true", "resolutionNote.specified=false");
    }

    @Test
    @Transactional
    void getAllRepairRequestsByResolutionNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where resolutionNote contains
        defaultRepairRequestFiltering(
            "resolutionNote.contains=" + DEFAULT_RESOLUTION_NOTE,
            "resolutionNote.contains=" + UPDATED_RESOLUTION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByResolutionNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        // Get all the repairRequestList where resolutionNote does not contain
        defaultRepairRequestFiltering(
            "resolutionNote.doesNotContain=" + UPDATED_RESOLUTION_NOTE,
            "resolutionNote.doesNotContain=" + DEFAULT_RESOLUTION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllRepairRequestsByRequesterIsEqualToSomething() throws Exception {
        Employee requester;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            repairRequestRepository.saveAndFlush(repairRequest);
            requester = EmployeeResourceIT.createEntity();
        } else {
            requester = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(requester);
        em.flush();
        repairRequest.setRequester(requester);
        repairRequestRepository.saveAndFlush(repairRequest);
        Long requesterId = requester.getId();
        // Get all the repairRequestList where requester equals to requesterId
        defaultRepairRequestShouldBeFound("requesterId.equals=" + requesterId);

        // Get all the repairRequestList where requester equals to (requesterId + 1)
        defaultRepairRequestShouldNotBeFound("requesterId.equals=" + (requesterId + 1));
    }

    @Test
    @Transactional
    void getAllRepairRequestsByEquipmentIsEqualToSomething() throws Exception {
        Equipment equipment;
        if (TestUtil.findAll(em, Equipment.class).isEmpty()) {
            repairRequestRepository.saveAndFlush(repairRequest);
            equipment = EquipmentResourceIT.createEntity();
        } else {
            equipment = TestUtil.findAll(em, Equipment.class).get(0);
        }
        em.persist(equipment);
        em.flush();
        repairRequest.setEquipment(equipment);
        repairRequestRepository.saveAndFlush(repairRequest);
        Long equipmentId = equipment.getId();
        // Get all the repairRequestList where equipment equals to equipmentId
        defaultRepairRequestShouldBeFound("equipmentId.equals=" + equipmentId);

        // Get all the repairRequestList where equipment equals to (equipmentId + 1)
        defaultRepairRequestShouldNotBeFound("equipmentId.equals=" + (equipmentId + 1));
    }

    private void defaultRepairRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultRepairRequestShouldBeFound(shouldBeFound);
        defaultRepairRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRepairRequestShouldBeFound(String filter) throws Exception {
        restRepairRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(repairRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].problemCategory").value(hasItem(DEFAULT_PROBLEM_CATEGORY)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].resolutionNote").value(hasItem(DEFAULT_RESOLUTION_NOTE)));

        // Check, that the count call also returns 1
        restRepairRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRepairRequestShouldNotBeFound(String filter) throws Exception {
        restRepairRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRepairRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRepairRequest() throws Exception {
        // Get the repairRequest
        restRepairRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRepairRequest() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the repairRequest
        RepairRequest updatedRepairRequest = repairRequestRepository.findById(repairRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRepairRequest are not directly saved in db
        em.detach(updatedRepairRequest);
        updatedRepairRequest
            .code(UPDATED_CODE)
            .requestDate(UPDATED_REQUEST_DATE)
            .problemCategory(UPDATED_PROBLEM_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(updatedRepairRequest);

        restRepairRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, repairRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(repairRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRepairRequestToMatchAllProperties(updatedRepairRequest);
    }

    @Test
    @Transactional
    void putNonExistingRepairRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        repairRequest.setId(longCount.incrementAndGet());

        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRepairRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, repairRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(repairRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRepairRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        repairRequest.setId(longCount.incrementAndGet());

        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(repairRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRepairRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        repairRequest.setId(longCount.incrementAndGet());

        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(repairRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRepairRequestWithPatch() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the repairRequest using partial update
        RepairRequest partialUpdatedRepairRequest = new RepairRequest();
        partialUpdatedRepairRequest.setId(repairRequest.getId());

        partialUpdatedRepairRequest.code(UPDATED_CODE).problemCategory(UPDATED_PROBLEM_CATEGORY);

        restRepairRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRepairRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRepairRequest))
            )
            .andExpect(status().isOk());

        // Validate the RepairRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRepairRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRepairRequest, repairRequest),
            getPersistedRepairRequest(repairRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateRepairRequestWithPatch() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the repairRequest using partial update
        RepairRequest partialUpdatedRepairRequest = new RepairRequest();
        partialUpdatedRepairRequest.setId(repairRequest.getId());

        partialUpdatedRepairRequest
            .code(UPDATED_CODE)
            .requestDate(UPDATED_REQUEST_DATE)
            .problemCategory(UPDATED_PROBLEM_CATEGORY)
            .description(UPDATED_DESCRIPTION)
            .status(UPDATED_STATUS)
            .resolutionNote(UPDATED_RESOLUTION_NOTE);

        restRepairRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRepairRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRepairRequest))
            )
            .andExpect(status().isOk());

        // Validate the RepairRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRepairRequestUpdatableFieldsEquals(partialUpdatedRepairRequest, getPersistedRepairRequest(partialUpdatedRepairRequest));
    }

    @Test
    @Transactional
    void patchNonExistingRepairRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        repairRequest.setId(longCount.incrementAndGet());

        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRepairRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, repairRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(repairRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRepairRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        repairRequest.setId(longCount.incrementAndGet());

        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(repairRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRepairRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        repairRequest.setId(longCount.incrementAndGet());

        // Create the RepairRequest
        RepairRequestDTO repairRequestDTO = repairRequestMapper.toDto(repairRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRepairRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(repairRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RepairRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRepairRequest() throws Exception {
        // Initialize the database
        insertedRepairRequest = repairRequestRepository.saveAndFlush(repairRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the repairRequest
        restRepairRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, repairRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return repairRequestRepository.count();
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

    protected RepairRequest getPersistedRepairRequest(RepairRequest repairRequest) {
        return repairRequestRepository.findById(repairRequest.getId()).orElseThrow();
    }

    protected void assertPersistedRepairRequestToMatchAllProperties(RepairRequest expectedRepairRequest) {
        assertRepairRequestAllPropertiesEquals(expectedRepairRequest, getPersistedRepairRequest(expectedRepairRequest));
    }

    protected void assertPersistedRepairRequestToMatchUpdatableProperties(RepairRequest expectedRepairRequest) {
        assertRepairRequestAllUpdatablePropertiesEquals(expectedRepairRequest, getPersistedRepairRequest(expectedRepairRequest));
    }
}

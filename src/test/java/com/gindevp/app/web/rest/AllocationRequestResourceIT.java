package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.AllocationRequestAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AllocationRequest;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.enumeration.AllocationRequestStatus;
import com.gindevp.app.repository.AllocationRequestRepository;
import com.gindevp.app.service.AllocationRequestService;
import com.gindevp.app.service.dto.AllocationRequestDTO;
import com.gindevp.app.service.mapper.AllocationRequestMapper;
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
 * Integration tests for the {@link AllocationRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AllocationRequestResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final AllocationRequestStatus DEFAULT_STATUS = AllocationRequestStatus.PENDING;
    private static final AllocationRequestStatus UPDATED_STATUS = AllocationRequestStatus.APPROVED;

    private static final String DEFAULT_BENEFICIARY_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_BENEFICIARY_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/allocation-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AllocationRequestRepository allocationRequestRepository;

    @Mock
    private AllocationRequestRepository allocationRequestRepositoryMock;

    @Autowired
    private AllocationRequestMapper allocationRequestMapper;

    @Mock
    private AllocationRequestService allocationRequestServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAllocationRequestMockMvc;

    private AllocationRequest allocationRequest;

    private AllocationRequest insertedAllocationRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AllocationRequest createEntity() {
        return new AllocationRequest()
            .code(DEFAULT_CODE)
            .requestDate(DEFAULT_REQUEST_DATE)
            .reason(DEFAULT_REASON)
            .status(DEFAULT_STATUS)
            .beneficiaryNote(DEFAULT_BENEFICIARY_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AllocationRequest createUpdatedEntity() {
        return new AllocationRequest()
            .code(UPDATED_CODE)
            .requestDate(UPDATED_REQUEST_DATE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .beneficiaryNote(UPDATED_BENEFICIARY_NOTE);
    }

    @BeforeEach
    public void initTest() {
        allocationRequest = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAllocationRequest != null) {
            allocationRequestRepository.delete(insertedAllocationRequest);
            insertedAllocationRequest = null;
        }
    }

    @Test
    @Transactional
    void createAllocationRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);
        var returnedAllocationRequestDTO = om.readValue(
            restAllocationRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AllocationRequestDTO.class
        );

        // Validate the AllocationRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAllocationRequest = allocationRequestMapper.toEntity(returnedAllocationRequestDTO);
        assertAllocationRequestUpdatableFieldsEquals(returnedAllocationRequest, getPersistedAllocationRequest(returnedAllocationRequest));

        insertedAllocationRequest = returnedAllocationRequest;
    }

    @Test
    @Transactional
    void createAllocationRequestWithExistingId() throws Exception {
        // Create the AllocationRequest with an existing ID
        allocationRequest.setId(1L);
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAllocationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        allocationRequest.setCode(null);

        // Create the AllocationRequest, which fails.
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        restAllocationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequestDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        allocationRequest.setRequestDate(null);

        // Create the AllocationRequest, which fails.
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        restAllocationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        allocationRequest.setStatus(null);

        // Create the AllocationRequest, which fails.
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        restAllocationRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAllocationRequests() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList
        restAllocationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(allocationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].beneficiaryNote").value(hasItem(DEFAULT_BENEFICIARY_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAllocationRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(allocationRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAllocationRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(allocationRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAllocationRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(allocationRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAllocationRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(allocationRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAllocationRequest() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get the allocationRequest
        restAllocationRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, allocationRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(allocationRequest.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.requestDate").value(DEFAULT_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.beneficiaryNote").value(DEFAULT_BENEFICIARY_NOTE));
    }

    @Test
    @Transactional
    void getAllocationRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        Long id = allocationRequest.getId();

        defaultAllocationRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAllocationRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAllocationRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where code equals to
        defaultAllocationRequestFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where code in
        defaultAllocationRequestFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where code is not null
        defaultAllocationRequestFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where code contains
        defaultAllocationRequestFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where code does not contain
        defaultAllocationRequestFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByRequestDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where requestDate equals to
        defaultAllocationRequestFiltering("requestDate.equals=" + DEFAULT_REQUEST_DATE, "requestDate.equals=" + UPDATED_REQUEST_DATE);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByRequestDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where requestDate in
        defaultAllocationRequestFiltering(
            "requestDate.in=" + DEFAULT_REQUEST_DATE + "," + UPDATED_REQUEST_DATE,
            "requestDate.in=" + UPDATED_REQUEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByRequestDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where requestDate is not null
        defaultAllocationRequestFiltering("requestDate.specified=true", "requestDate.specified=false");
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByReasonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where reason equals to
        defaultAllocationRequestFiltering("reason.equals=" + DEFAULT_REASON, "reason.equals=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByReasonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where reason in
        defaultAllocationRequestFiltering("reason.in=" + DEFAULT_REASON + "," + UPDATED_REASON, "reason.in=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByReasonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where reason is not null
        defaultAllocationRequestFiltering("reason.specified=true", "reason.specified=false");
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByReasonContainsSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where reason contains
        defaultAllocationRequestFiltering("reason.contains=" + DEFAULT_REASON, "reason.contains=" + UPDATED_REASON);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByReasonNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where reason does not contain
        defaultAllocationRequestFiltering("reason.doesNotContain=" + UPDATED_REASON, "reason.doesNotContain=" + DEFAULT_REASON);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where status equals to
        defaultAllocationRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where status in
        defaultAllocationRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where status is not null
        defaultAllocationRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByBeneficiaryNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where beneficiaryNote equals to
        defaultAllocationRequestFiltering(
            "beneficiaryNote.equals=" + DEFAULT_BENEFICIARY_NOTE,
            "beneficiaryNote.equals=" + UPDATED_BENEFICIARY_NOTE
        );
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByBeneficiaryNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where beneficiaryNote in
        defaultAllocationRequestFiltering(
            "beneficiaryNote.in=" + DEFAULT_BENEFICIARY_NOTE + "," + UPDATED_BENEFICIARY_NOTE,
            "beneficiaryNote.in=" + UPDATED_BENEFICIARY_NOTE
        );
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByBeneficiaryNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where beneficiaryNote is not null
        defaultAllocationRequestFiltering("beneficiaryNote.specified=true", "beneficiaryNote.specified=false");
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByBeneficiaryNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where beneficiaryNote contains
        defaultAllocationRequestFiltering(
            "beneficiaryNote.contains=" + DEFAULT_BENEFICIARY_NOTE,
            "beneficiaryNote.contains=" + UPDATED_BENEFICIARY_NOTE
        );
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByBeneficiaryNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        // Get all the allocationRequestList where beneficiaryNote does not contain
        defaultAllocationRequestFiltering(
            "beneficiaryNote.doesNotContain=" + UPDATED_BENEFICIARY_NOTE,
            "beneficiaryNote.doesNotContain=" + DEFAULT_BENEFICIARY_NOTE
        );
    }

    @Test
    @Transactional
    void getAllAllocationRequestsByRequesterIsEqualToSomething() throws Exception {
        Employee requester;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            allocationRequestRepository.saveAndFlush(allocationRequest);
            requester = EmployeeResourceIT.createEntity();
        } else {
            requester = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(requester);
        em.flush();
        allocationRequest.setRequester(requester);
        allocationRequestRepository.saveAndFlush(allocationRequest);
        Long requesterId = requester.getId();
        // Get all the allocationRequestList where requester equals to requesterId
        defaultAllocationRequestShouldBeFound("requesterId.equals=" + requesterId);

        // Get all the allocationRequestList where requester equals to (requesterId + 1)
        defaultAllocationRequestShouldNotBeFound("requesterId.equals=" + (requesterId + 1));
    }

    private void defaultAllocationRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAllocationRequestShouldBeFound(shouldBeFound);
        defaultAllocationRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAllocationRequestShouldBeFound(String filter) throws Exception {
        restAllocationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(allocationRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].beneficiaryNote").value(hasItem(DEFAULT_BENEFICIARY_NOTE)));

        // Check, that the count call also returns 1
        restAllocationRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAllocationRequestShouldNotBeFound(String filter) throws Exception {
        restAllocationRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAllocationRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAllocationRequest() throws Exception {
        // Get the allocationRequest
        restAllocationRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAllocationRequest() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the allocationRequest
        AllocationRequest updatedAllocationRequest = allocationRequestRepository.findById(allocationRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAllocationRequest are not directly saved in db
        em.detach(updatedAllocationRequest);
        updatedAllocationRequest
            .code(UPDATED_CODE)
            .requestDate(UPDATED_REQUEST_DATE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .beneficiaryNote(UPDATED_BENEFICIARY_NOTE);
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(updatedAllocationRequest);

        restAllocationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, allocationRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(allocationRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAllocationRequestToMatchAllProperties(updatedAllocationRequest);
    }

    @Test
    @Transactional
    void putNonExistingAllocationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequest.setId(longCount.incrementAndGet());

        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAllocationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, allocationRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(allocationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAllocationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequest.setId(longCount.incrementAndGet());

        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(allocationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAllocationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequest.setId(longCount.incrementAndGet());

        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAllocationRequestWithPatch() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the allocationRequest using partial update
        AllocationRequest partialUpdatedAllocationRequest = new AllocationRequest();
        partialUpdatedAllocationRequest.setId(allocationRequest.getId());

        partialUpdatedAllocationRequest.reason(UPDATED_REASON).status(UPDATED_STATUS).beneficiaryNote(UPDATED_BENEFICIARY_NOTE);

        restAllocationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAllocationRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAllocationRequest))
            )
            .andExpect(status().isOk());

        // Validate the AllocationRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAllocationRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAllocationRequest, allocationRequest),
            getPersistedAllocationRequest(allocationRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateAllocationRequestWithPatch() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the allocationRequest using partial update
        AllocationRequest partialUpdatedAllocationRequest = new AllocationRequest();
        partialUpdatedAllocationRequest.setId(allocationRequest.getId());

        partialUpdatedAllocationRequest
            .code(UPDATED_CODE)
            .requestDate(UPDATED_REQUEST_DATE)
            .reason(UPDATED_REASON)
            .status(UPDATED_STATUS)
            .beneficiaryNote(UPDATED_BENEFICIARY_NOTE);

        restAllocationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAllocationRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAllocationRequest))
            )
            .andExpect(status().isOk());

        // Validate the AllocationRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAllocationRequestUpdatableFieldsEquals(
            partialUpdatedAllocationRequest,
            getPersistedAllocationRequest(partialUpdatedAllocationRequest)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAllocationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequest.setId(longCount.incrementAndGet());

        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAllocationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, allocationRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(allocationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAllocationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequest.setId(longCount.incrementAndGet());

        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(allocationRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAllocationRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequest.setId(longCount.incrementAndGet());

        // Create the AllocationRequest
        AllocationRequestDTO allocationRequestDTO = allocationRequestMapper.toDto(allocationRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(allocationRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AllocationRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAllocationRequest() throws Exception {
        // Initialize the database
        insertedAllocationRequest = allocationRequestRepository.saveAndFlush(allocationRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the allocationRequest
        restAllocationRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, allocationRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return allocationRequestRepository.count();
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

    protected AllocationRequest getPersistedAllocationRequest(AllocationRequest allocationRequest) {
        return allocationRequestRepository.findById(allocationRequest.getId()).orElseThrow();
    }

    protected void assertPersistedAllocationRequestToMatchAllProperties(AllocationRequest expectedAllocationRequest) {
        assertAllocationRequestAllPropertiesEquals(expectedAllocationRequest, getPersistedAllocationRequest(expectedAllocationRequest));
    }

    protected void assertPersistedAllocationRequestToMatchUpdatableProperties(AllocationRequest expectedAllocationRequest) {
        assertAllocationRequestAllUpdatablePropertiesEquals(
            expectedAllocationRequest,
            getPersistedAllocationRequest(expectedAllocationRequest)
        );
    }
}

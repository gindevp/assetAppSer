package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.ReturnRequestAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.ReturnRequest;
import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import com.gindevp.app.repository.ReturnRequestRepository;
import com.gindevp.app.service.ReturnRequestService;
import com.gindevp.app.service.dto.ReturnRequestDTO;
import com.gindevp.app.service.mapper.ReturnRequestMapper;
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
 * Integration tests for the {@link ReturnRequestResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReturnRequestResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_REQUEST_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_REQUEST_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final ReturnRequestStatus DEFAULT_STATUS = ReturnRequestStatus.PENDING;
    private static final ReturnRequestStatus UPDATED_STATUS = ReturnRequestStatus.APPROVED;

    private static final String ENTITY_API_URL = "/api/return-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnRequestRepository returnRequestRepository;

    @Mock
    private ReturnRequestRepository returnRequestRepositoryMock;

    @Autowired
    private ReturnRequestMapper returnRequestMapper;

    @Mock
    private ReturnRequestService returnRequestServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnRequestMockMvc;

    private ReturnRequest returnRequest;

    private ReturnRequest insertedReturnRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnRequest createEntity() {
        return new ReturnRequest().code(DEFAULT_CODE).requestDate(DEFAULT_REQUEST_DATE).note(DEFAULT_NOTE).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnRequest createUpdatedEntity() {
        return new ReturnRequest().code(UPDATED_CODE).requestDate(UPDATED_REQUEST_DATE).note(UPDATED_NOTE).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        returnRequest = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReturnRequest != null) {
            returnRequestRepository.delete(insertedReturnRequest);
            insertedReturnRequest = null;
        }
    }

    @Test
    @Transactional
    void createReturnRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);
        var returnedReturnRequestDTO = om.readValue(
            restReturnRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnRequestDTO.class
        );

        // Validate the ReturnRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnRequest = returnRequestMapper.toEntity(returnedReturnRequestDTO);
        assertReturnRequestUpdatableFieldsEquals(returnedReturnRequest, getPersistedReturnRequest(returnedReturnRequest));

        insertedReturnRequest = returnedReturnRequest;
    }

    @Test
    @Transactional
    void createReturnRequestWithExistingId() throws Exception {
        // Create the ReturnRequest with an existing ID
        returnRequest.setId(1L);
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnRequest.setCode(null);

        // Create the ReturnRequest, which fails.
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        restReturnRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRequestDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnRequest.setRequestDate(null);

        // Create the ReturnRequest, which fails.
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        restReturnRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnRequest.setStatus(null);

        // Create the ReturnRequest, which fails.
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        restReturnRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReturnRequests() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList
        restReturnRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnRequestsWithEagerRelationshipsIsEnabled() throws Exception {
        when(returnRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(returnRequestServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnRequestsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(returnRequestServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnRequestMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(returnRequestRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReturnRequest() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get the returnRequest
        restReturnRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, returnRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnRequest.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.requestDate").value(DEFAULT_REQUEST_DATE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    void getReturnRequestsByIdFiltering() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        Long id = returnRequest.getId();

        defaultReturnRequestFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultReturnRequestFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultReturnRequestFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where code equals to
        defaultReturnRequestFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where code in
        defaultReturnRequestFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where code is not null
        defaultReturnRequestFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnRequestsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where code contains
        defaultReturnRequestFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where code does not contain
        defaultReturnRequestFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByRequestDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where requestDate equals to
        defaultReturnRequestFiltering("requestDate.equals=" + DEFAULT_REQUEST_DATE, "requestDate.equals=" + UPDATED_REQUEST_DATE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByRequestDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where requestDate in
        defaultReturnRequestFiltering(
            "requestDate.in=" + DEFAULT_REQUEST_DATE + "," + UPDATED_REQUEST_DATE,
            "requestDate.in=" + UPDATED_REQUEST_DATE
        );
    }

    @Test
    @Transactional
    void getAllReturnRequestsByRequestDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where requestDate is not null
        defaultReturnRequestFiltering("requestDate.specified=true", "requestDate.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnRequestsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where note equals to
        defaultReturnRequestFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where note in
        defaultReturnRequestFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where note is not null
        defaultReturnRequestFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnRequestsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where note contains
        defaultReturnRequestFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where note does not contain
        defaultReturnRequestFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where status equals to
        defaultReturnRequestFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where status in
        defaultReturnRequestFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllReturnRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        // Get all the returnRequestList where status is not null
        defaultReturnRequestFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllReturnRequestsByRequesterIsEqualToSomething() throws Exception {
        Employee requester;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            returnRequestRepository.saveAndFlush(returnRequest);
            requester = EmployeeResourceIT.createEntity();
        } else {
            requester = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(requester);
        em.flush();
        returnRequest.setRequester(requester);
        returnRequestRepository.saveAndFlush(returnRequest);
        Long requesterId = requester.getId();
        // Get all the returnRequestList where requester equals to requesterId
        defaultReturnRequestShouldBeFound("requesterId.equals=" + requesterId);

        // Get all the returnRequestList where requester equals to (requesterId + 1)
        defaultReturnRequestShouldNotBeFound("requesterId.equals=" + (requesterId + 1));
    }

    private void defaultReturnRequestFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultReturnRequestShouldBeFound(shouldBeFound);
        defaultReturnRequestShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultReturnRequestShouldBeFound(String filter) throws Exception {
        restReturnRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].requestDate").value(hasItem(DEFAULT_REQUEST_DATE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));

        // Check, that the count call also returns 1
        restReturnRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultReturnRequestShouldNotBeFound(String filter) throws Exception {
        restReturnRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restReturnRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingReturnRequest() throws Exception {
        // Get the returnRequest
        restReturnRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnRequest() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnRequest
        ReturnRequest updatedReturnRequest = returnRequestRepository.findById(returnRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnRequest are not directly saved in db
        em.detach(updatedReturnRequest);
        updatedReturnRequest.code(UPDATED_CODE).requestDate(UPDATED_REQUEST_DATE).note(UPDATED_NOTE).status(UPDATED_STATUS);
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(updatedReturnRequest);

        restReturnRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnRequestToMatchAllProperties(updatedReturnRequest);
    }

    @Test
    @Transactional
    void putNonExistingReturnRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequest.setId(longCount.incrementAndGet());

        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequest.setId(longCount.incrementAndGet());

        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequest.setId(longCount.incrementAndGet());

        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnRequestWithPatch() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnRequest using partial update
        ReturnRequest partialUpdatedReturnRequest = new ReturnRequest();
        partialUpdatedReturnRequest.setId(returnRequest.getId());

        partialUpdatedReturnRequest.code(UPDATED_CODE);

        restReturnRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnRequest))
            )
            .andExpect(status().isOk());

        // Validate the ReturnRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnRequest, returnRequest),
            getPersistedReturnRequest(returnRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnRequestWithPatch() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnRequest using partial update
        ReturnRequest partialUpdatedReturnRequest = new ReturnRequest();
        partialUpdatedReturnRequest.setId(returnRequest.getId());

        partialUpdatedReturnRequest.code(UPDATED_CODE).requestDate(UPDATED_REQUEST_DATE).note(UPDATED_NOTE).status(UPDATED_STATUS);

        restReturnRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnRequest))
            )
            .andExpect(status().isOk());

        // Validate the ReturnRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnRequestUpdatableFieldsEquals(partialUpdatedReturnRequest, getPersistedReturnRequest(partialUpdatedReturnRequest));
    }

    @Test
    @Transactional
    void patchNonExistingReturnRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequest.setId(longCount.incrementAndGet());

        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequest.setId(longCount.incrementAndGet());

        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequest.setId(longCount.incrementAndGet());

        // Create the ReturnRequest
        ReturnRequestDTO returnRequestDTO = returnRequestMapper.toDto(returnRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnRequest() throws Exception {
        // Initialize the database
        insertedReturnRequest = returnRequestRepository.saveAndFlush(returnRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnRequest
        restReturnRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnRequestRepository.count();
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

    protected ReturnRequest getPersistedReturnRequest(ReturnRequest returnRequest) {
        return returnRequestRepository.findById(returnRequest.getId()).orElseThrow();
    }

    protected void assertPersistedReturnRequestToMatchAllProperties(ReturnRequest expectedReturnRequest) {
        assertReturnRequestAllPropertiesEquals(expectedReturnRequest, getPersistedReturnRequest(expectedReturnRequest));
    }

    protected void assertPersistedReturnRequestToMatchUpdatableProperties(ReturnRequest expectedReturnRequest) {
        assertReturnRequestAllUpdatablePropertiesEquals(expectedReturnRequest, getPersistedReturnRequest(expectedReturnRequest));
    }
}

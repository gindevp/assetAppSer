package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.ConsumableAssignmentAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.ConsumableAssignment;
import com.gindevp.app.repository.ConsumableAssignmentRepository;
import com.gindevp.app.service.ConsumableAssignmentService;
import com.gindevp.app.service.dto.ConsumableAssignmentDTO;
import com.gindevp.app.service.mapper.ConsumableAssignmentMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ConsumableAssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ConsumableAssignmentResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final LocalDate DEFAULT_ASSIGNED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ASSIGNED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Integer DEFAULT_RETURNED_QUANTITY = 0;
    private static final Integer UPDATED_RETURNED_QUANTITY = 1;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/consumable-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConsumableAssignmentRepository consumableAssignmentRepository;

    @Mock
    private ConsumableAssignmentRepository consumableAssignmentRepositoryMock;

    @Autowired
    private ConsumableAssignmentMapper consumableAssignmentMapper;

    @Mock
    private ConsumableAssignmentService consumableAssignmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConsumableAssignmentMockMvc;

    private ConsumableAssignment consumableAssignment;

    private ConsumableAssignment insertedConsumableAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConsumableAssignment createEntity() {
        return new ConsumableAssignment()
            .quantity(DEFAULT_QUANTITY)
            .assignedDate(DEFAULT_ASSIGNED_DATE)
            .returnedQuantity(DEFAULT_RETURNED_QUANTITY)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConsumableAssignment createUpdatedEntity() {
        return new ConsumableAssignment()
            .quantity(UPDATED_QUANTITY)
            .assignedDate(UPDATED_ASSIGNED_DATE)
            .returnedQuantity(UPDATED_RETURNED_QUANTITY)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        consumableAssignment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedConsumableAssignment != null) {
            consumableAssignmentRepository.delete(insertedConsumableAssignment);
            insertedConsumableAssignment = null;
        }
    }

    @Test
    @Transactional
    void createConsumableAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);
        var returnedConsumableAssignmentDTO = om.readValue(
            restConsumableAssignmentMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableAssignmentDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConsumableAssignmentDTO.class
        );

        // Validate the ConsumableAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConsumableAssignment = consumableAssignmentMapper.toEntity(returnedConsumableAssignmentDTO);
        assertConsumableAssignmentUpdatableFieldsEquals(
            returnedConsumableAssignment,
            getPersistedConsumableAssignment(returnedConsumableAssignment)
        );

        insertedConsumableAssignment = returnedConsumableAssignment;
    }

    @Test
    @Transactional
    void createConsumableAssignmentWithExistingId() throws Exception {
        // Create the ConsumableAssignment with an existing ID
        consumableAssignment.setId(1L);
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConsumableAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableAssignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        consumableAssignment.setQuantity(null);

        // Create the ConsumableAssignment, which fails.
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        restConsumableAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableAssignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssignedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        consumableAssignment.setAssignedDate(null);

        // Create the ConsumableAssignment, which fails.
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        restConsumableAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableAssignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConsumableAssignments() throws Exception {
        // Initialize the database
        insertedConsumableAssignment = consumableAssignmentRepository.saveAndFlush(consumableAssignment);

        // Get all the consumableAssignmentList
        restConsumableAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consumableAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].assignedDate").value(hasItem(DEFAULT_ASSIGNED_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnedQuantity").value(hasItem(DEFAULT_RETURNED_QUANTITY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllConsumableAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(consumableAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restConsumableAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(consumableAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllConsumableAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(consumableAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restConsumableAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(consumableAssignmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getConsumableAssignment() throws Exception {
        // Initialize the database
        insertedConsumableAssignment = consumableAssignmentRepository.saveAndFlush(consumableAssignment);

        // Get the consumableAssignment
        restConsumableAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, consumableAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(consumableAssignment.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.assignedDate").value(DEFAULT_ASSIGNED_DATE.toString()))
            .andExpect(jsonPath("$.returnedQuantity").value(DEFAULT_RETURNED_QUANTITY))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingConsumableAssignment() throws Exception {
        // Get the consumableAssignment
        restConsumableAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConsumableAssignment() throws Exception {
        // Initialize the database
        insertedConsumableAssignment = consumableAssignmentRepository.saveAndFlush(consumableAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consumableAssignment
        ConsumableAssignment updatedConsumableAssignment = consumableAssignmentRepository
            .findById(consumableAssignment.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedConsumableAssignment are not directly saved in db
        em.detach(updatedConsumableAssignment);
        updatedConsumableAssignment
            .quantity(UPDATED_QUANTITY)
            .assignedDate(UPDATED_ASSIGNED_DATE)
            .returnedQuantity(UPDATED_RETURNED_QUANTITY)
            .note(UPDATED_NOTE);
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(updatedConsumableAssignment);

        restConsumableAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consumableAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consumableAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConsumableAssignmentToMatchAllProperties(updatedConsumableAssignment);
    }

    @Test
    @Transactional
    void putNonExistingConsumableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableAssignment.setId(longCount.incrementAndGet());

        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsumableAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consumableAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consumableAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConsumableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableAssignment.setId(longCount.incrementAndGet());

        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consumableAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConsumableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableAssignment.setId(longCount.incrementAndGet());

        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConsumableAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedConsumableAssignment = consumableAssignmentRepository.saveAndFlush(consumableAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consumableAssignment using partial update
        ConsumableAssignment partialUpdatedConsumableAssignment = new ConsumableAssignment();
        partialUpdatedConsumableAssignment.setId(consumableAssignment.getId());

        partialUpdatedConsumableAssignment.assignedDate(UPDATED_ASSIGNED_DATE).note(UPDATED_NOTE);

        restConsumableAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsumableAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsumableAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ConsumableAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsumableAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConsumableAssignment, consumableAssignment),
            getPersistedConsumableAssignment(consumableAssignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateConsumableAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedConsumableAssignment = consumableAssignmentRepository.saveAndFlush(consumableAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consumableAssignment using partial update
        ConsumableAssignment partialUpdatedConsumableAssignment = new ConsumableAssignment();
        partialUpdatedConsumableAssignment.setId(consumableAssignment.getId());

        partialUpdatedConsumableAssignment
            .quantity(UPDATED_QUANTITY)
            .assignedDate(UPDATED_ASSIGNED_DATE)
            .returnedQuantity(UPDATED_RETURNED_QUANTITY)
            .note(UPDATED_NOTE);

        restConsumableAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsumableAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsumableAssignment))
            )
            .andExpect(status().isOk());

        // Validate the ConsumableAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsumableAssignmentUpdatableFieldsEquals(
            partialUpdatedConsumableAssignment,
            getPersistedConsumableAssignment(partialUpdatedConsumableAssignment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingConsumableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableAssignment.setId(longCount.incrementAndGet());

        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsumableAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, consumableAssignmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consumableAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConsumableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableAssignment.setId(longCount.incrementAndGet());

        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consumableAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConsumableAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableAssignment.setId(longCount.incrementAndGet());

        // Create the ConsumableAssignment
        ConsumableAssignmentDTO consumableAssignmentDTO = consumableAssignmentMapper.toDto(consumableAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(consumableAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ConsumableAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConsumableAssignment() throws Exception {
        // Initialize the database
        insertedConsumableAssignment = consumableAssignmentRepository.saveAndFlush(consumableAssignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the consumableAssignment
        restConsumableAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, consumableAssignment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return consumableAssignmentRepository.count();
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

    protected ConsumableAssignment getPersistedConsumableAssignment(ConsumableAssignment consumableAssignment) {
        return consumableAssignmentRepository.findById(consumableAssignment.getId()).orElseThrow();
    }

    protected void assertPersistedConsumableAssignmentToMatchAllProperties(ConsumableAssignment expectedConsumableAssignment) {
        assertConsumableAssignmentAllPropertiesEquals(
            expectedConsumableAssignment,
            getPersistedConsumableAssignment(expectedConsumableAssignment)
        );
    }

    protected void assertPersistedConsumableAssignmentToMatchUpdatableProperties(ConsumableAssignment expectedConsumableAssignment) {
        assertConsumableAssignmentAllUpdatablePropertiesEquals(
            expectedConsumableAssignment,
            getPersistedConsumableAssignment(expectedConsumableAssignment)
        );
    }
}

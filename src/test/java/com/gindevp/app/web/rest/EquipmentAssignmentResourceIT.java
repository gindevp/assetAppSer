package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.EquipmentAssignmentAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.EquipmentAssignment;
import com.gindevp.app.repository.EquipmentAssignmentRepository;
import com.gindevp.app.service.EquipmentAssignmentService;
import com.gindevp.app.service.dto.EquipmentAssignmentDTO;
import com.gindevp.app.service.mapper.EquipmentAssignmentMapper;
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
 * Integration tests for the {@link EquipmentAssignmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EquipmentAssignmentResourceIT {

    private static final LocalDate DEFAULT_ASSIGNED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ASSIGNED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RETURNED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURNED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/equipment-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EquipmentAssignmentRepository equipmentAssignmentRepository;

    @Mock
    private EquipmentAssignmentRepository equipmentAssignmentRepositoryMock;

    @Autowired
    private EquipmentAssignmentMapper equipmentAssignmentMapper;

    @Mock
    private EquipmentAssignmentService equipmentAssignmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEquipmentAssignmentMockMvc;

    private EquipmentAssignment equipmentAssignment;

    private EquipmentAssignment insertedEquipmentAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EquipmentAssignment createEntity() {
        return new EquipmentAssignment().assignedDate(DEFAULT_ASSIGNED_DATE).returnedDate(DEFAULT_RETURNED_DATE).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EquipmentAssignment createUpdatedEntity() {
        return new EquipmentAssignment().assignedDate(UPDATED_ASSIGNED_DATE).returnedDate(UPDATED_RETURNED_DATE).note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        equipmentAssignment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEquipmentAssignment != null) {
            equipmentAssignmentRepository.delete(insertedEquipmentAssignment);
            insertedEquipmentAssignment = null;
        }
    }

    @Test
    @Transactional
    void createEquipmentAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);
        var returnedEquipmentAssignmentDTO = om.readValue(
            restEquipmentAssignmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentAssignmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EquipmentAssignmentDTO.class
        );

        // Validate the EquipmentAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEquipmentAssignment = equipmentAssignmentMapper.toEntity(returnedEquipmentAssignmentDTO);
        assertEquipmentAssignmentUpdatableFieldsEquals(
            returnedEquipmentAssignment,
            getPersistedEquipmentAssignment(returnedEquipmentAssignment)
        );

        insertedEquipmentAssignment = returnedEquipmentAssignment;
    }

    @Test
    @Transactional
    void createEquipmentAssignmentWithExistingId() throws Exception {
        // Create the EquipmentAssignment with an existing ID
        equipmentAssignment.setId(1L);
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEquipmentAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentAssignmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAssignedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipmentAssignment.setAssignedDate(null);

        // Create the EquipmentAssignment, which fails.
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        restEquipmentAssignmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentAssignmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEquipmentAssignments() throws Exception {
        // Initialize the database
        insertedEquipmentAssignment = equipmentAssignmentRepository.saveAndFlush(equipmentAssignment);

        // Get all the equipmentAssignmentList
        restEquipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipmentAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedDate").value(hasItem(DEFAULT_ASSIGNED_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnedDate").value(hasItem(DEFAULT_RETURNED_DATE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentAssignmentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(equipmentAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipmentAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(equipmentAssignmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentAssignmentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(equipmentAssignmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipmentAssignmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(equipmentAssignmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEquipmentAssignment() throws Exception {
        // Initialize the database
        insertedEquipmentAssignment = equipmentAssignmentRepository.saveAndFlush(equipmentAssignment);

        // Get the equipmentAssignment
        restEquipmentAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, equipmentAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(equipmentAssignment.getId().intValue()))
            .andExpect(jsonPath("$.assignedDate").value(DEFAULT_ASSIGNED_DATE.toString()))
            .andExpect(jsonPath("$.returnedDate").value(DEFAULT_RETURNED_DATE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingEquipmentAssignment() throws Exception {
        // Get the equipmentAssignment
        restEquipmentAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEquipmentAssignment() throws Exception {
        // Initialize the database
        insertedEquipmentAssignment = equipmentAssignmentRepository.saveAndFlush(equipmentAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentAssignment
        EquipmentAssignment updatedEquipmentAssignment = equipmentAssignmentRepository.findById(equipmentAssignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEquipmentAssignment are not directly saved in db
        em.detach(updatedEquipmentAssignment);
        updatedEquipmentAssignment.assignedDate(UPDATED_ASSIGNED_DATE).returnedDate(UPDATED_RETURNED_DATE).note(UPDATED_NOTE);
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(updatedEquipmentAssignment);

        restEquipmentAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipmentAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipmentAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEquipmentAssignmentToMatchAllProperties(updatedEquipmentAssignment);
    }

    @Test
    @Transactional
    void putNonExistingEquipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentAssignment.setId(longCount.incrementAndGet());

        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipmentAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipmentAssignmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEquipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentAssignment.setId(longCount.incrementAndGet());

        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEquipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentAssignment.setId(longCount.incrementAndGet());

        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentAssignmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentAssignmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEquipmentAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedEquipmentAssignment = equipmentAssignmentRepository.saveAndFlush(equipmentAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentAssignment using partial update
        EquipmentAssignment partialUpdatedEquipmentAssignment = new EquipmentAssignment();
        partialUpdatedEquipmentAssignment.setId(equipmentAssignment.getId());

        partialUpdatedEquipmentAssignment.assignedDate(UPDATED_ASSIGNED_DATE).returnedDate(UPDATED_RETURNED_DATE).note(UPDATED_NOTE);

        restEquipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipmentAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEquipmentAssignment))
            )
            .andExpect(status().isOk());

        // Validate the EquipmentAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEquipmentAssignment, equipmentAssignment),
            getPersistedEquipmentAssignment(equipmentAssignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateEquipmentAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedEquipmentAssignment = equipmentAssignmentRepository.saveAndFlush(equipmentAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipmentAssignment using partial update
        EquipmentAssignment partialUpdatedEquipmentAssignment = new EquipmentAssignment();
        partialUpdatedEquipmentAssignment.setId(equipmentAssignment.getId());

        partialUpdatedEquipmentAssignment.assignedDate(UPDATED_ASSIGNED_DATE).returnedDate(UPDATED_RETURNED_DATE).note(UPDATED_NOTE);

        restEquipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipmentAssignment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEquipmentAssignment))
            )
            .andExpect(status().isOk());

        // Validate the EquipmentAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentAssignmentUpdatableFieldsEquals(
            partialUpdatedEquipmentAssignment,
            getPersistedEquipmentAssignment(partialUpdatedEquipmentAssignment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEquipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentAssignment.setId(longCount.incrementAndGet());

        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, equipmentAssignmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(equipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEquipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentAssignment.setId(longCount.incrementAndGet());

        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(equipmentAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEquipmentAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipmentAssignment.setId(longCount.incrementAndGet());

        // Create the EquipmentAssignment
        EquipmentAssignmentDTO equipmentAssignmentDTO = equipmentAssignmentMapper.toDto(equipmentAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(equipmentAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EquipmentAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEquipmentAssignment() throws Exception {
        // Initialize the database
        insertedEquipmentAssignment = equipmentAssignmentRepository.saveAndFlush(equipmentAssignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the equipmentAssignment
        restEquipmentAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, equipmentAssignment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return equipmentAssignmentRepository.count();
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

    protected EquipmentAssignment getPersistedEquipmentAssignment(EquipmentAssignment equipmentAssignment) {
        return equipmentAssignmentRepository.findById(equipmentAssignment.getId()).orElseThrow();
    }

    protected void assertPersistedEquipmentAssignmentToMatchAllProperties(EquipmentAssignment expectedEquipmentAssignment) {
        assertEquipmentAssignmentAllPropertiesEquals(
            expectedEquipmentAssignment,
            getPersistedEquipmentAssignment(expectedEquipmentAssignment)
        );
    }

    protected void assertPersistedEquipmentAssignmentToMatchUpdatableProperties(EquipmentAssignment expectedEquipmentAssignment) {
        assertEquipmentAssignmentAllUpdatablePropertiesEquals(
            expectedEquipmentAssignment,
            getPersistedEquipmentAssignment(expectedEquipmentAssignment)
        );
    }
}

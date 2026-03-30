package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.AllocationRequestLineAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AllocationRequestLine;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.repository.AllocationRequestLineRepository;
import com.gindevp.app.service.AllocationRequestLineService;
import com.gindevp.app.service.dto.AllocationRequestLineDTO;
import com.gindevp.app.service.mapper.AllocationRequestLineMapper;
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
 * Integration tests for the {@link AllocationRequestLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AllocationRequestLineResourceIT {

    private static final Integer DEFAULT_LINE_NO = 1;
    private static final Integer UPDATED_LINE_NO = 2;

    private static final AssetManagementType DEFAULT_LINE_TYPE = AssetManagementType.DEVICE;
    private static final AssetManagementType UPDATED_LINE_TYPE = AssetManagementType.CONSUMABLE;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/allocation-request-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AllocationRequestLineRepository allocationRequestLineRepository;

    @Mock
    private AllocationRequestLineRepository allocationRequestLineRepositoryMock;

    @Autowired
    private AllocationRequestLineMapper allocationRequestLineMapper;

    @Mock
    private AllocationRequestLineService allocationRequestLineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAllocationRequestLineMockMvc;

    private AllocationRequestLine allocationRequestLine;

    private AllocationRequestLine insertedAllocationRequestLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AllocationRequestLine createEntity() {
        return new AllocationRequestLine()
            .lineNo(DEFAULT_LINE_NO)
            .lineType(DEFAULT_LINE_TYPE)
            .quantity(DEFAULT_QUANTITY)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AllocationRequestLine createUpdatedEntity() {
        return new AllocationRequestLine()
            .lineNo(UPDATED_LINE_NO)
            .lineType(UPDATED_LINE_TYPE)
            .quantity(UPDATED_QUANTITY)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        allocationRequestLine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedAllocationRequestLine != null) {
            allocationRequestLineRepository.delete(insertedAllocationRequestLine);
            insertedAllocationRequestLine = null;
        }
    }

    @Test
    @Transactional
    void createAllocationRequestLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);
        var returnedAllocationRequestLineDTO = om.readValue(
            restAllocationRequestLineMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestLineDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AllocationRequestLineDTO.class
        );

        // Validate the AllocationRequestLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAllocationRequestLine = allocationRequestLineMapper.toEntity(returnedAllocationRequestLineDTO);
        assertAllocationRequestLineUpdatableFieldsEquals(
            returnedAllocationRequestLine,
            getPersistedAllocationRequestLine(returnedAllocationRequestLine)
        );

        insertedAllocationRequestLine = returnedAllocationRequestLine;
    }

    @Test
    @Transactional
    void createAllocationRequestLineWithExistingId() throws Exception {
        // Create the AllocationRequestLine with an existing ID
        allocationRequestLine.setId(1L);
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAllocationRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        allocationRequestLine.setLineNo(null);

        // Create the AllocationRequestLine, which fails.
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        restAllocationRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLineTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        allocationRequestLine.setLineType(null);

        // Create the AllocationRequestLine, which fails.
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        restAllocationRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAllocationRequestLines() throws Exception {
        // Initialize the database
        insertedAllocationRequestLine = allocationRequestLineRepository.saveAndFlush(allocationRequestLine);

        // Get all the allocationRequestLineList
        restAllocationRequestLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(allocationRequestLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNo").value(hasItem(DEFAULT_LINE_NO)))
            .andExpect(jsonPath("$.[*].lineType").value(hasItem(DEFAULT_LINE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAllocationRequestLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(allocationRequestLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAllocationRequestLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(allocationRequestLineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAllocationRequestLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(allocationRequestLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAllocationRequestLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(allocationRequestLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAllocationRequestLine() throws Exception {
        // Initialize the database
        insertedAllocationRequestLine = allocationRequestLineRepository.saveAndFlush(allocationRequestLine);

        // Get the allocationRequestLine
        restAllocationRequestLineMockMvc
            .perform(get(ENTITY_API_URL_ID, allocationRequestLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(allocationRequestLine.getId().intValue()))
            .andExpect(jsonPath("$.lineNo").value(DEFAULT_LINE_NO))
            .andExpect(jsonPath("$.lineType").value(DEFAULT_LINE_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingAllocationRequestLine() throws Exception {
        // Get the allocationRequestLine
        restAllocationRequestLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAllocationRequestLine() throws Exception {
        // Initialize the database
        insertedAllocationRequestLine = allocationRequestLineRepository.saveAndFlush(allocationRequestLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the allocationRequestLine
        AllocationRequestLine updatedAllocationRequestLine = allocationRequestLineRepository
            .findById(allocationRequestLine.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedAllocationRequestLine are not directly saved in db
        em.detach(updatedAllocationRequestLine);
        updatedAllocationRequestLine.lineNo(UPDATED_LINE_NO).lineType(UPDATED_LINE_TYPE).quantity(UPDATED_QUANTITY).note(UPDATED_NOTE);
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(updatedAllocationRequestLine);

        restAllocationRequestLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, allocationRequestLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(allocationRequestLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAllocationRequestLineToMatchAllProperties(updatedAllocationRequestLine);
    }

    @Test
    @Transactional
    void putNonExistingAllocationRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequestLine.setId(longCount.incrementAndGet());

        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAllocationRequestLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, allocationRequestLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(allocationRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAllocationRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequestLine.setId(longCount.incrementAndGet());

        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(allocationRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAllocationRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequestLine.setId(longCount.incrementAndGet());

        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(allocationRequestLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAllocationRequestLineWithPatch() throws Exception {
        // Initialize the database
        insertedAllocationRequestLine = allocationRequestLineRepository.saveAndFlush(allocationRequestLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the allocationRequestLine using partial update
        AllocationRequestLine partialUpdatedAllocationRequestLine = new AllocationRequestLine();
        partialUpdatedAllocationRequestLine.setId(allocationRequestLine.getId());

        partialUpdatedAllocationRequestLine.lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).note(UPDATED_NOTE);

        restAllocationRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAllocationRequestLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAllocationRequestLine))
            )
            .andExpect(status().isOk());

        // Validate the AllocationRequestLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAllocationRequestLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAllocationRequestLine, allocationRequestLine),
            getPersistedAllocationRequestLine(allocationRequestLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateAllocationRequestLineWithPatch() throws Exception {
        // Initialize the database
        insertedAllocationRequestLine = allocationRequestLineRepository.saveAndFlush(allocationRequestLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the allocationRequestLine using partial update
        AllocationRequestLine partialUpdatedAllocationRequestLine = new AllocationRequestLine();
        partialUpdatedAllocationRequestLine.setId(allocationRequestLine.getId());

        partialUpdatedAllocationRequestLine
            .lineNo(UPDATED_LINE_NO)
            .lineType(UPDATED_LINE_TYPE)
            .quantity(UPDATED_QUANTITY)
            .note(UPDATED_NOTE);

        restAllocationRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAllocationRequestLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAllocationRequestLine))
            )
            .andExpect(status().isOk());

        // Validate the AllocationRequestLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAllocationRequestLineUpdatableFieldsEquals(
            partialUpdatedAllocationRequestLine,
            getPersistedAllocationRequestLine(partialUpdatedAllocationRequestLine)
        );
    }

    @Test
    @Transactional
    void patchNonExistingAllocationRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequestLine.setId(longCount.incrementAndGet());

        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAllocationRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, allocationRequestLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(allocationRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAllocationRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequestLine.setId(longCount.incrementAndGet());

        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(allocationRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAllocationRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        allocationRequestLine.setId(longCount.incrementAndGet());

        // Create the AllocationRequestLine
        AllocationRequestLineDTO allocationRequestLineDTO = allocationRequestLineMapper.toDto(allocationRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAllocationRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(allocationRequestLineDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AllocationRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAllocationRequestLine() throws Exception {
        // Initialize the database
        insertedAllocationRequestLine = allocationRequestLineRepository.saveAndFlush(allocationRequestLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the allocationRequestLine
        restAllocationRequestLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, allocationRequestLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return allocationRequestLineRepository.count();
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

    protected AllocationRequestLine getPersistedAllocationRequestLine(AllocationRequestLine allocationRequestLine) {
        return allocationRequestLineRepository.findById(allocationRequestLine.getId()).orElseThrow();
    }

    protected void assertPersistedAllocationRequestLineToMatchAllProperties(AllocationRequestLine expectedAllocationRequestLine) {
        assertAllocationRequestLineAllPropertiesEquals(
            expectedAllocationRequestLine,
            getPersistedAllocationRequestLine(expectedAllocationRequestLine)
        );
    }

    protected void assertPersistedAllocationRequestLineToMatchUpdatableProperties(AllocationRequestLine expectedAllocationRequestLine) {
        assertAllocationRequestLineAllUpdatablePropertiesEquals(
            expectedAllocationRequestLine,
            getPersistedAllocationRequestLine(expectedAllocationRequestLine)
        );
    }
}

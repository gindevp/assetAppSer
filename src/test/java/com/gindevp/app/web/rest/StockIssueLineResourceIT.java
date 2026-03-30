package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.StockIssueLineAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.StockIssueLine;
import com.gindevp.app.repository.StockIssueLineRepository;
import com.gindevp.app.service.StockIssueLineService;
import com.gindevp.app.service.dto.StockIssueLineDTO;
import com.gindevp.app.service.mapper.StockIssueLineMapper;
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
 * Integration tests for the {@link StockIssueLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockIssueLineResourceIT {

    private static final Integer DEFAULT_LINE_NO = 1;
    private static final Integer UPDATED_LINE_NO = 2;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-issue-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockIssueLineRepository stockIssueLineRepository;

    @Mock
    private StockIssueLineRepository stockIssueLineRepositoryMock;

    @Autowired
    private StockIssueLineMapper stockIssueLineMapper;

    @Mock
    private StockIssueLineService stockIssueLineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockIssueLineMockMvc;

    private StockIssueLine stockIssueLine;

    private StockIssueLine insertedStockIssueLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockIssueLine createEntity() {
        return new StockIssueLine().lineNo(DEFAULT_LINE_NO).quantity(DEFAULT_QUANTITY).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockIssueLine createUpdatedEntity() {
        return new StockIssueLine().lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        stockIssueLine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStockIssueLine != null) {
            stockIssueLineRepository.delete(insertedStockIssueLine);
            insertedStockIssueLine = null;
        }
    }

    @Test
    @Transactional
    void createStockIssueLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);
        var returnedStockIssueLineDTO = om.readValue(
            restStockIssueLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueLineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockIssueLineDTO.class
        );

        // Validate the StockIssueLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockIssueLine = stockIssueLineMapper.toEntity(returnedStockIssueLineDTO);
        assertStockIssueLineUpdatableFieldsEquals(returnedStockIssueLine, getPersistedStockIssueLine(returnedStockIssueLine));

        insertedStockIssueLine = returnedStockIssueLine;
    }

    @Test
    @Transactional
    void createStockIssueLineWithExistingId() throws Exception {
        // Create the StockIssueLine with an existing ID
        stockIssueLine.setId(1L);
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockIssueLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockIssueLine.setLineNo(null);

        // Create the StockIssueLine, which fails.
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        restStockIssueLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockIssueLine.setQuantity(null);

        // Create the StockIssueLine, which fails.
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        restStockIssueLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockIssueLines() throws Exception {
        // Initialize the database
        insertedStockIssueLine = stockIssueLineRepository.saveAndFlush(stockIssueLine);

        // Get all the stockIssueLineList
        restStockIssueLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockIssueLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNo").value(hasItem(DEFAULT_LINE_NO)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockIssueLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockIssueLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockIssueLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockIssueLineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockIssueLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockIssueLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockIssueLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockIssueLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockIssueLine() throws Exception {
        // Initialize the database
        insertedStockIssueLine = stockIssueLineRepository.saveAndFlush(stockIssueLine);

        // Get the stockIssueLine
        restStockIssueLineMockMvc
            .perform(get(ENTITY_API_URL_ID, stockIssueLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockIssueLine.getId().intValue()))
            .andExpect(jsonPath("$.lineNo").value(DEFAULT_LINE_NO))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingStockIssueLine() throws Exception {
        // Get the stockIssueLine
        restStockIssueLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockIssueLine() throws Exception {
        // Initialize the database
        insertedStockIssueLine = stockIssueLineRepository.saveAndFlush(stockIssueLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockIssueLine
        StockIssueLine updatedStockIssueLine = stockIssueLineRepository.findById(stockIssueLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockIssueLine are not directly saved in db
        em.detach(updatedStockIssueLine);
        updatedStockIssueLine.lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).note(UPDATED_NOTE);
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(updatedStockIssueLine);

        restStockIssueLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockIssueLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockIssueLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockIssueLineToMatchAllProperties(updatedStockIssueLine);
    }

    @Test
    @Transactional
    void putNonExistingStockIssueLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssueLine.setId(longCount.incrementAndGet());

        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockIssueLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockIssueLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockIssueLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockIssueLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssueLine.setId(longCount.incrementAndGet());

        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockIssueLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockIssueLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssueLine.setId(longCount.incrementAndGet());

        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockIssueLineWithPatch() throws Exception {
        // Initialize the database
        insertedStockIssueLine = stockIssueLineRepository.saveAndFlush(stockIssueLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockIssueLine using partial update
        StockIssueLine partialUpdatedStockIssueLine = new StockIssueLine();
        partialUpdatedStockIssueLine.setId(stockIssueLine.getId());

        partialUpdatedStockIssueLine.quantity(UPDATED_QUANTITY);

        restStockIssueLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockIssueLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockIssueLine))
            )
            .andExpect(status().isOk());

        // Validate the StockIssueLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockIssueLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockIssueLine, stockIssueLine),
            getPersistedStockIssueLine(stockIssueLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockIssueLineWithPatch() throws Exception {
        // Initialize the database
        insertedStockIssueLine = stockIssueLineRepository.saveAndFlush(stockIssueLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockIssueLine using partial update
        StockIssueLine partialUpdatedStockIssueLine = new StockIssueLine();
        partialUpdatedStockIssueLine.setId(stockIssueLine.getId());

        partialUpdatedStockIssueLine.lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).note(UPDATED_NOTE);

        restStockIssueLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockIssueLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockIssueLine))
            )
            .andExpect(status().isOk());

        // Validate the StockIssueLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockIssueLineUpdatableFieldsEquals(partialUpdatedStockIssueLine, getPersistedStockIssueLine(partialUpdatedStockIssueLine));
    }

    @Test
    @Transactional
    void patchNonExistingStockIssueLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssueLine.setId(longCount.incrementAndGet());

        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockIssueLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockIssueLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockIssueLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockIssueLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssueLine.setId(longCount.incrementAndGet());

        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockIssueLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockIssueLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssueLine.setId(longCount.incrementAndGet());

        // Create the StockIssueLine
        StockIssueLineDTO stockIssueLineDTO = stockIssueLineMapper.toDto(stockIssueLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockIssueLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockIssueLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockIssueLine() throws Exception {
        // Initialize the database
        insertedStockIssueLine = stockIssueLineRepository.saveAndFlush(stockIssueLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockIssueLine
        restStockIssueLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockIssueLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockIssueLineRepository.count();
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

    protected StockIssueLine getPersistedStockIssueLine(StockIssueLine stockIssueLine) {
        return stockIssueLineRepository.findById(stockIssueLine.getId()).orElseThrow();
    }

    protected void assertPersistedStockIssueLineToMatchAllProperties(StockIssueLine expectedStockIssueLine) {
        assertStockIssueLineAllPropertiesEquals(expectedStockIssueLine, getPersistedStockIssueLine(expectedStockIssueLine));
    }

    protected void assertPersistedStockIssueLineToMatchUpdatableProperties(StockIssueLine expectedStockIssueLine) {
        assertStockIssueLineAllUpdatablePropertiesEquals(expectedStockIssueLine, getPersistedStockIssueLine(expectedStockIssueLine));
    }
}

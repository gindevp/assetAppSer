package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.StockReceiptLineAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.gindevp.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.StockReceiptLine;
import com.gindevp.app.repository.StockReceiptLineRepository;
import com.gindevp.app.service.StockReceiptLineService;
import com.gindevp.app.service.dto.StockReceiptLineDTO;
import com.gindevp.app.service.mapper.StockReceiptLineMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link StockReceiptLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockReceiptLineResourceIT {

    private static final Integer DEFAULT_LINE_NO = 1;
    private static final Integer UPDATED_LINE_NO = 2;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(1);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-receipt-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockReceiptLineRepository stockReceiptLineRepository;

    @Mock
    private StockReceiptLineRepository stockReceiptLineRepositoryMock;

    @Autowired
    private StockReceiptLineMapper stockReceiptLineMapper;

    @Mock
    private StockReceiptLineService stockReceiptLineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockReceiptLineMockMvc;

    private StockReceiptLine stockReceiptLine;

    private StockReceiptLine insertedStockReceiptLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockReceiptLine createEntity() {
        return new StockReceiptLine().lineNo(DEFAULT_LINE_NO).quantity(DEFAULT_QUANTITY).unitPrice(DEFAULT_UNIT_PRICE).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockReceiptLine createUpdatedEntity() {
        return new StockReceiptLine().lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        stockReceiptLine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStockReceiptLine != null) {
            stockReceiptLineRepository.delete(insertedStockReceiptLine);
            insertedStockReceiptLine = null;
        }
    }

    @Test
    @Transactional
    void createStockReceiptLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);
        var returnedStockReceiptLineDTO = om.readValue(
            restStockReceiptLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptLineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockReceiptLineDTO.class
        );

        // Validate the StockReceiptLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockReceiptLine = stockReceiptLineMapper.toEntity(returnedStockReceiptLineDTO);
        assertStockReceiptLineUpdatableFieldsEquals(returnedStockReceiptLine, getPersistedStockReceiptLine(returnedStockReceiptLine));

        insertedStockReceiptLine = returnedStockReceiptLine;
    }

    @Test
    @Transactional
    void createStockReceiptLineWithExistingId() throws Exception {
        // Create the StockReceiptLine with an existing ID
        stockReceiptLine.setId(1L);
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockReceiptLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockReceiptLine.setLineNo(null);

        // Create the StockReceiptLine, which fails.
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        restStockReceiptLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockReceiptLine.setQuantity(null);

        // Create the StockReceiptLine, which fails.
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        restStockReceiptLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockReceiptLines() throws Exception {
        // Initialize the database
        insertedStockReceiptLine = stockReceiptLineRepository.saveAndFlush(stockReceiptLine);

        // Get all the stockReceiptLineList
        restStockReceiptLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockReceiptLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNo").value(hasItem(DEFAULT_LINE_NO)))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(sameNumber(DEFAULT_UNIT_PRICE))))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockReceiptLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockReceiptLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockReceiptLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockReceiptLineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockReceiptLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockReceiptLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockReceiptLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockReceiptLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockReceiptLine() throws Exception {
        // Initialize the database
        insertedStockReceiptLine = stockReceiptLineRepository.saveAndFlush(stockReceiptLine);

        // Get the stockReceiptLine
        restStockReceiptLineMockMvc
            .perform(get(ENTITY_API_URL_ID, stockReceiptLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockReceiptLine.getId().intValue()))
            .andExpect(jsonPath("$.lineNo").value(DEFAULT_LINE_NO))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.unitPrice").value(sameNumber(DEFAULT_UNIT_PRICE)))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingStockReceiptLine() throws Exception {
        // Get the stockReceiptLine
        restStockReceiptLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockReceiptLine() throws Exception {
        // Initialize the database
        insertedStockReceiptLine = stockReceiptLineRepository.saveAndFlush(stockReceiptLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockReceiptLine
        StockReceiptLine updatedStockReceiptLine = stockReceiptLineRepository.findById(stockReceiptLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockReceiptLine are not directly saved in db
        em.detach(updatedStockReceiptLine);
        updatedStockReceiptLine.lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).note(UPDATED_NOTE);
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(updatedStockReceiptLine);

        restStockReceiptLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockReceiptLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockReceiptLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockReceiptLineToMatchAllProperties(updatedStockReceiptLine);
    }

    @Test
    @Transactional
    void putNonExistingStockReceiptLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceiptLine.setId(longCount.incrementAndGet());

        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockReceiptLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockReceiptLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockReceiptLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockReceiptLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceiptLine.setId(longCount.incrementAndGet());

        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockReceiptLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockReceiptLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceiptLine.setId(longCount.incrementAndGet());

        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockReceiptLineWithPatch() throws Exception {
        // Initialize the database
        insertedStockReceiptLine = stockReceiptLineRepository.saveAndFlush(stockReceiptLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockReceiptLine using partial update
        StockReceiptLine partialUpdatedStockReceiptLine = new StockReceiptLine();
        partialUpdatedStockReceiptLine.setId(stockReceiptLine.getId());

        restStockReceiptLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockReceiptLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockReceiptLine))
            )
            .andExpect(status().isOk());

        // Validate the StockReceiptLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockReceiptLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockReceiptLine, stockReceiptLine),
            getPersistedStockReceiptLine(stockReceiptLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockReceiptLineWithPatch() throws Exception {
        // Initialize the database
        insertedStockReceiptLine = stockReceiptLineRepository.saveAndFlush(stockReceiptLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockReceiptLine using partial update
        StockReceiptLine partialUpdatedStockReceiptLine = new StockReceiptLine();
        partialUpdatedStockReceiptLine.setId(stockReceiptLine.getId());

        partialUpdatedStockReceiptLine.lineNo(UPDATED_LINE_NO).quantity(UPDATED_QUANTITY).unitPrice(UPDATED_UNIT_PRICE).note(UPDATED_NOTE);

        restStockReceiptLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockReceiptLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockReceiptLine))
            )
            .andExpect(status().isOk());

        // Validate the StockReceiptLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockReceiptLineUpdatableFieldsEquals(
            partialUpdatedStockReceiptLine,
            getPersistedStockReceiptLine(partialUpdatedStockReceiptLine)
        );
    }

    @Test
    @Transactional
    void patchNonExistingStockReceiptLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceiptLine.setId(longCount.incrementAndGet());

        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockReceiptLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockReceiptLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockReceiptLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockReceiptLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceiptLine.setId(longCount.incrementAndGet());

        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockReceiptLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockReceiptLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceiptLine.setId(longCount.incrementAndGet());

        // Create the StockReceiptLine
        StockReceiptLineDTO stockReceiptLineDTO = stockReceiptLineMapper.toDto(stockReceiptLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockReceiptLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockReceiptLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockReceiptLine() throws Exception {
        // Initialize the database
        insertedStockReceiptLine = stockReceiptLineRepository.saveAndFlush(stockReceiptLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockReceiptLine
        restStockReceiptLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockReceiptLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockReceiptLineRepository.count();
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

    protected StockReceiptLine getPersistedStockReceiptLine(StockReceiptLine stockReceiptLine) {
        return stockReceiptLineRepository.findById(stockReceiptLine.getId()).orElseThrow();
    }

    protected void assertPersistedStockReceiptLineToMatchAllProperties(StockReceiptLine expectedStockReceiptLine) {
        assertStockReceiptLineAllPropertiesEquals(expectedStockReceiptLine, getPersistedStockReceiptLine(expectedStockReceiptLine));
    }

    protected void assertPersistedStockReceiptLineToMatchUpdatableProperties(StockReceiptLine expectedStockReceiptLine) {
        assertStockReceiptLineAllUpdatablePropertiesEquals(
            expectedStockReceiptLine,
            getPersistedStockReceiptLine(expectedStockReceiptLine)
        );
    }
}

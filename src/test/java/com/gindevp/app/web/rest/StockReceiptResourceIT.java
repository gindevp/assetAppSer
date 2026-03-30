package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.StockReceiptAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.StockReceipt;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.domain.enumeration.StockReceiptSource;
import com.gindevp.app.repository.StockReceiptRepository;
import com.gindevp.app.service.dto.StockReceiptDTO;
import com.gindevp.app.service.mapper.StockReceiptMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link StockReceiptResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class StockReceiptResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_RECEIPT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RECEIPT_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_RECEIPT_DATE = LocalDate.ofEpochDay(-1L);

    private static final StockReceiptSource DEFAULT_SOURCE = StockReceiptSource.NEW_PURCHASE;
    private static final StockReceiptSource UPDATED_SOURCE = StockReceiptSource.RECOVERY;

    private static final DocumentStatus DEFAULT_STATUS = DocumentStatus.DRAFT;
    private static final DocumentStatus UPDATED_STATUS = DocumentStatus.CONFIRMED;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-receipts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockReceiptRepository stockReceiptRepository;

    @Autowired
    private StockReceiptMapper stockReceiptMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockReceiptMockMvc;

    private StockReceipt stockReceipt;

    private StockReceipt insertedStockReceipt;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockReceipt createEntity() {
        return new StockReceipt()
            .code(DEFAULT_CODE)
            .receiptDate(DEFAULT_RECEIPT_DATE)
            .source(DEFAULT_SOURCE)
            .status(DEFAULT_STATUS)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockReceipt createUpdatedEntity() {
        return new StockReceipt()
            .code(UPDATED_CODE)
            .receiptDate(UPDATED_RECEIPT_DATE)
            .source(UPDATED_SOURCE)
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        stockReceipt = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStockReceipt != null) {
            stockReceiptRepository.delete(insertedStockReceipt);
            insertedStockReceipt = null;
        }
    }

    @Test
    @Transactional
    void createStockReceipt() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);
        var returnedStockReceiptDTO = om.readValue(
            restStockReceiptMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockReceiptDTO.class
        );

        // Validate the StockReceipt in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockReceipt = stockReceiptMapper.toEntity(returnedStockReceiptDTO);
        assertStockReceiptUpdatableFieldsEquals(returnedStockReceipt, getPersistedStockReceipt(returnedStockReceipt));

        insertedStockReceipt = returnedStockReceipt;
    }

    @Test
    @Transactional
    void createStockReceiptWithExistingId() throws Exception {
        // Create the StockReceipt with an existing ID
        stockReceipt.setId(1L);
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockReceipt.setCode(null);

        // Create the StockReceipt, which fails.
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        restStockReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceiptDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockReceipt.setReceiptDate(null);

        // Create the StockReceipt, which fails.
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        restStockReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSourceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockReceipt.setSource(null);

        // Create the StockReceipt, which fails.
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        restStockReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockReceipt.setStatus(null);

        // Create the StockReceipt, which fails.
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        restStockReceiptMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockReceipts() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList
        restStockReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].receiptDate").value(hasItem(DEFAULT_RECEIPT_DATE.toString())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getStockReceipt() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get the stockReceipt
        restStockReceiptMockMvc
            .perform(get(ENTITY_API_URL_ID, stockReceipt.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockReceipt.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.receiptDate").value(DEFAULT_RECEIPT_DATE.toString()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getStockReceiptsByIdFiltering() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        Long id = stockReceipt.getId();

        defaultStockReceiptFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStockReceiptFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStockReceiptFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where code equals to
        defaultStockReceiptFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where code in
        defaultStockReceiptFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where code is not null
        defaultStockReceiptFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllStockReceiptsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where code contains
        defaultStockReceiptFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where code does not contain
        defaultStockReceiptFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate equals to
        defaultStockReceiptFiltering("receiptDate.equals=" + DEFAULT_RECEIPT_DATE, "receiptDate.equals=" + UPDATED_RECEIPT_DATE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate in
        defaultStockReceiptFiltering(
            "receiptDate.in=" + DEFAULT_RECEIPT_DATE + "," + UPDATED_RECEIPT_DATE,
            "receiptDate.in=" + UPDATED_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate is not null
        defaultStockReceiptFiltering("receiptDate.specified=true", "receiptDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate is greater than or equal to
        defaultStockReceiptFiltering(
            "receiptDate.greaterThanOrEqual=" + DEFAULT_RECEIPT_DATE,
            "receiptDate.greaterThanOrEqual=" + UPDATED_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate is less than or equal to
        defaultStockReceiptFiltering(
            "receiptDate.lessThanOrEqual=" + DEFAULT_RECEIPT_DATE,
            "receiptDate.lessThanOrEqual=" + SMALLER_RECEIPT_DATE
        );
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate is less than
        defaultStockReceiptFiltering("receiptDate.lessThan=" + UPDATED_RECEIPT_DATE, "receiptDate.lessThan=" + DEFAULT_RECEIPT_DATE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByReceiptDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where receiptDate is greater than
        defaultStockReceiptFiltering("receiptDate.greaterThan=" + SMALLER_RECEIPT_DATE, "receiptDate.greaterThan=" + DEFAULT_RECEIPT_DATE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsBySourceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where source equals to
        defaultStockReceiptFiltering("source.equals=" + DEFAULT_SOURCE, "source.equals=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsBySourceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where source in
        defaultStockReceiptFiltering("source.in=" + DEFAULT_SOURCE + "," + UPDATED_SOURCE, "source.in=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsBySourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where source is not null
        defaultStockReceiptFiltering("source.specified=true", "source.specified=false");
    }

    @Test
    @Transactional
    void getAllStockReceiptsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where status equals to
        defaultStockReceiptFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where status in
        defaultStockReceiptFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where status is not null
        defaultStockReceiptFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllStockReceiptsByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where note equals to
        defaultStockReceiptFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where note in
        defaultStockReceiptFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where note is not null
        defaultStockReceiptFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllStockReceiptsByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where note contains
        defaultStockReceiptFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllStockReceiptsByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        // Get all the stockReceiptList where note does not contain
        defaultStockReceiptFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    private void defaultStockReceiptFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStockReceiptShouldBeFound(shouldBeFound);
        defaultStockReceiptShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockReceiptShouldBeFound(String filter) throws Exception {
        restStockReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockReceipt.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].receiptDate").value(hasItem(DEFAULT_RECEIPT_DATE.toString())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restStockReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockReceiptShouldNotBeFound(String filter) throws Exception {
        restStockReceiptMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockReceiptMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockReceipt() throws Exception {
        // Get the stockReceipt
        restStockReceiptMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockReceipt() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockReceipt
        StockReceipt updatedStockReceipt = stockReceiptRepository.findById(stockReceipt.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockReceipt are not directly saved in db
        em.detach(updatedStockReceipt);
        updatedStockReceipt
            .code(UPDATED_CODE)
            .receiptDate(UPDATED_RECEIPT_DATE)
            .source(UPDATED_SOURCE)
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE);
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(updatedStockReceipt);

        restStockReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockReceiptDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockReceiptToMatchAllProperties(updatedStockReceipt);
    }

    @Test
    @Transactional
    void putNonExistingStockReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceipt.setId(longCount.incrementAndGet());

        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockReceiptDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceipt.setId(longCount.incrementAndGet());

        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceipt.setId(longCount.incrementAndGet());

        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockReceipt using partial update
        StockReceipt partialUpdatedStockReceipt = new StockReceipt();
        partialUpdatedStockReceipt.setId(stockReceipt.getId());

        partialUpdatedStockReceipt.receiptDate(UPDATED_RECEIPT_DATE);

        restStockReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockReceipt))
            )
            .andExpect(status().isOk());

        // Validate the StockReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockReceiptUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockReceipt, stockReceipt),
            getPersistedStockReceipt(stockReceipt)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockReceiptWithPatch() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockReceipt using partial update
        StockReceipt partialUpdatedStockReceipt = new StockReceipt();
        partialUpdatedStockReceipt.setId(stockReceipt.getId());

        partialUpdatedStockReceipt
            .code(UPDATED_CODE)
            .receiptDate(UPDATED_RECEIPT_DATE)
            .source(UPDATED_SOURCE)
            .status(UPDATED_STATUS)
            .note(UPDATED_NOTE);

        restStockReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockReceipt.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockReceipt))
            )
            .andExpect(status().isOk());

        // Validate the StockReceipt in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockReceiptUpdatableFieldsEquals(partialUpdatedStockReceipt, getPersistedStockReceipt(partialUpdatedStockReceipt));
    }

    @Test
    @Transactional
    void patchNonExistingStockReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceipt.setId(longCount.incrementAndGet());

        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockReceiptDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceipt.setId(longCount.incrementAndGet());

        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockReceiptDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockReceipt() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockReceipt.setId(longCount.incrementAndGet());

        // Create the StockReceipt
        StockReceiptDTO stockReceiptDTO = stockReceiptMapper.toDto(stockReceipt);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockReceiptMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockReceiptDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockReceipt in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockReceipt() throws Exception {
        // Initialize the database
        insertedStockReceipt = stockReceiptRepository.saveAndFlush(stockReceipt);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockReceipt
        restStockReceiptMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockReceipt.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockReceiptRepository.count();
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

    protected StockReceipt getPersistedStockReceipt(StockReceipt stockReceipt) {
        return stockReceiptRepository.findById(stockReceipt.getId()).orElseThrow();
    }

    protected void assertPersistedStockReceiptToMatchAllProperties(StockReceipt expectedStockReceipt) {
        assertStockReceiptAllPropertiesEquals(expectedStockReceipt, getPersistedStockReceipt(expectedStockReceipt));
    }

    protected void assertPersistedStockReceiptToMatchUpdatableProperties(StockReceipt expectedStockReceipt) {
        assertStockReceiptAllUpdatablePropertiesEquals(expectedStockReceipt, getPersistedStockReceipt(expectedStockReceipt));
    }
}

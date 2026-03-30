package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.EquipmentAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.gindevp.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.Supplier;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.service.EquipmentService;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.mapper.EquipmentMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link EquipmentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class EquipmentResourceIT {

    private static final String DEFAULT_EQUIPMENT_CODE = "EQ000001";
    private static final String UPDATED_EQUIPMENT_CODE = "EQ000002";

    private static final String DEFAULT_SERIAL = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL = "BBBBBBBBBB";

    private static final String DEFAULT_CONDITION_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_CONDITION_NOTE = "BBBBBBBBBB";

    private static final EquipmentOperationalStatus DEFAULT_STATUS = EquipmentOperationalStatus.IN_STOCK;
    private static final EquipmentOperationalStatus UPDATED_STATUS = EquipmentOperationalStatus.IN_USE;

    private static final BigDecimal DEFAULT_PURCHASE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PURCHASE_PRICE = new BigDecimal(1);
    private static final BigDecimal SMALLER_PURCHASE_PRICE = new BigDecimal(0 - 1);

    private static final LocalDate DEFAULT_CAPITALIZATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CAPITALIZATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_CAPITALIZATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final Integer DEFAULT_DEPRECIATION_MONTHS = 0;
    private static final Integer UPDATED_DEPRECIATION_MONTHS = 1;
    private static final Integer SMALLER_DEPRECIATION_MONTHS = 0 - 1;

    private static final BigDecimal DEFAULT_SALVAGE_VALUE = new BigDecimal(0);
    private static final BigDecimal UPDATED_SALVAGE_VALUE = new BigDecimal(1);
    private static final BigDecimal SMALLER_SALVAGE_VALUE = new BigDecimal(0 - 1);

    private static final BigDecimal DEFAULT_BOOK_VALUE_SNAPSHOT = new BigDecimal(0);
    private static final BigDecimal UPDATED_BOOK_VALUE_SNAPSHOT = new BigDecimal(1);
    private static final BigDecimal SMALLER_BOOK_VALUE_SNAPSHOT = new BigDecimal(0 - 1);

    private static final String ENTITY_API_URL = "/api/equipment";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Mock
    private EquipmentRepository equipmentRepositoryMock;

    @Autowired
    private EquipmentMapper equipmentMapper;

    @Mock
    private EquipmentService equipmentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEquipmentMockMvc;

    private Equipment equipment;

    private Equipment insertedEquipment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipment createEntity() {
        return new Equipment()
            .equipmentCode(DEFAULT_EQUIPMENT_CODE)
            .serial(DEFAULT_SERIAL)
            .conditionNote(DEFAULT_CONDITION_NOTE)
            .status(DEFAULT_STATUS)
            .purchasePrice(DEFAULT_PURCHASE_PRICE)
            .capitalizationDate(DEFAULT_CAPITALIZATION_DATE)
            .depreciationMonths(DEFAULT_DEPRECIATION_MONTHS)
            .salvageValue(DEFAULT_SALVAGE_VALUE)
            .bookValueSnapshot(DEFAULT_BOOK_VALUE_SNAPSHOT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Equipment createUpdatedEntity() {
        return new Equipment()
            .equipmentCode(UPDATED_EQUIPMENT_CODE)
            .serial(UPDATED_SERIAL)
            .conditionNote(UPDATED_CONDITION_NOTE)
            .status(UPDATED_STATUS)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .capitalizationDate(UPDATED_CAPITALIZATION_DATE)
            .depreciationMonths(UPDATED_DEPRECIATION_MONTHS)
            .salvageValue(UPDATED_SALVAGE_VALUE)
            .bookValueSnapshot(UPDATED_BOOK_VALUE_SNAPSHOT);
    }

    @BeforeEach
    public void initTest() {
        equipment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEquipment != null) {
            equipmentRepository.delete(insertedEquipment);
            insertedEquipment = null;
        }
    }

    @Test
    @Transactional
    void createEquipment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);
        var returnedEquipmentDTO = om.readValue(
            restEquipmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EquipmentDTO.class
        );

        // Validate the Equipment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEquipment = equipmentMapper.toEntity(returnedEquipmentDTO);
        assertEquipmentUpdatableFieldsEquals(returnedEquipment, getPersistedEquipment(returnedEquipment));

        insertedEquipment = returnedEquipment;
    }

    @Test
    @Transactional
    void createEquipmentWithExistingId() throws Exception {
        // Create the Equipment with an existing ID
        equipment.setId(1L);
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEquipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkEquipmentCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipment.setEquipmentCode(null);

        // Create the Equipment, which fails.
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        restEquipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        equipment.setStatus(null);

        // Create the Equipment, which fails.
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        restEquipmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEquipment() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList
        restEquipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].equipmentCode").value(hasItem(DEFAULT_EQUIPMENT_CODE)))
            .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL)))
            .andExpect(jsonPath("$.[*].conditionNote").value(hasItem(DEFAULT_CONDITION_NOTE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].purchasePrice").value(hasItem(sameNumber(DEFAULT_PURCHASE_PRICE))))
            .andExpect(jsonPath("$.[*].capitalizationDate").value(hasItem(DEFAULT_CAPITALIZATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].depreciationMonths").value(hasItem(DEFAULT_DEPRECIATION_MONTHS)))
            .andExpect(jsonPath("$.[*].salvageValue").value(hasItem(sameNumber(DEFAULT_SALVAGE_VALUE))))
            .andExpect(jsonPath("$.[*].bookValueSnapshot").value(hasItem(sameNumber(DEFAULT_BOOK_VALUE_SNAPSHOT))));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentWithEagerRelationshipsIsEnabled() throws Exception {
        when(equipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(equipmentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEquipmentWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(equipmentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restEquipmentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(equipmentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getEquipment() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get the equipment
        restEquipmentMockMvc
            .perform(get(ENTITY_API_URL_ID, equipment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(equipment.getId().intValue()))
            .andExpect(jsonPath("$.equipmentCode").value(DEFAULT_EQUIPMENT_CODE))
            .andExpect(jsonPath("$.serial").value(DEFAULT_SERIAL))
            .andExpect(jsonPath("$.conditionNote").value(DEFAULT_CONDITION_NOTE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.purchasePrice").value(sameNumber(DEFAULT_PURCHASE_PRICE)))
            .andExpect(jsonPath("$.capitalizationDate").value(DEFAULT_CAPITALIZATION_DATE.toString()))
            .andExpect(jsonPath("$.depreciationMonths").value(DEFAULT_DEPRECIATION_MONTHS))
            .andExpect(jsonPath("$.salvageValue").value(sameNumber(DEFAULT_SALVAGE_VALUE)))
            .andExpect(jsonPath("$.bookValueSnapshot").value(sameNumber(DEFAULT_BOOK_VALUE_SNAPSHOT)));
    }

    @Test
    @Transactional
    void getEquipmentByIdFiltering() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        Long id = equipment.getId();

        defaultEquipmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEquipmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEquipmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEquipmentByEquipmentCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where equipmentCode equals to
        defaultEquipmentFiltering("equipmentCode.equals=" + DEFAULT_EQUIPMENT_CODE, "equipmentCode.equals=" + UPDATED_EQUIPMENT_CODE);
    }

    @Test
    @Transactional
    void getAllEquipmentByEquipmentCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where equipmentCode in
        defaultEquipmentFiltering(
            "equipmentCode.in=" + DEFAULT_EQUIPMENT_CODE + "," + UPDATED_EQUIPMENT_CODE,
            "equipmentCode.in=" + UPDATED_EQUIPMENT_CODE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByEquipmentCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where equipmentCode is not null
        defaultEquipmentFiltering("equipmentCode.specified=true", "equipmentCode.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByEquipmentCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where equipmentCode contains
        defaultEquipmentFiltering("equipmentCode.contains=" + DEFAULT_EQUIPMENT_CODE, "equipmentCode.contains=" + UPDATED_EQUIPMENT_CODE);
    }

    @Test
    @Transactional
    void getAllEquipmentByEquipmentCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where equipmentCode does not contain
        defaultEquipmentFiltering(
            "equipmentCode.doesNotContain=" + UPDATED_EQUIPMENT_CODE,
            "equipmentCode.doesNotContain=" + DEFAULT_EQUIPMENT_CODE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentBySerialIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where serial equals to
        defaultEquipmentFiltering("serial.equals=" + DEFAULT_SERIAL, "serial.equals=" + UPDATED_SERIAL);
    }

    @Test
    @Transactional
    void getAllEquipmentBySerialIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where serial in
        defaultEquipmentFiltering("serial.in=" + DEFAULT_SERIAL + "," + UPDATED_SERIAL, "serial.in=" + UPDATED_SERIAL);
    }

    @Test
    @Transactional
    void getAllEquipmentBySerialIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where serial is not null
        defaultEquipmentFiltering("serial.specified=true", "serial.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentBySerialContainsSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where serial contains
        defaultEquipmentFiltering("serial.contains=" + DEFAULT_SERIAL, "serial.contains=" + UPDATED_SERIAL);
    }

    @Test
    @Transactional
    void getAllEquipmentBySerialNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where serial does not contain
        defaultEquipmentFiltering("serial.doesNotContain=" + UPDATED_SERIAL, "serial.doesNotContain=" + DEFAULT_SERIAL);
    }

    @Test
    @Transactional
    void getAllEquipmentByConditionNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where conditionNote equals to
        defaultEquipmentFiltering("conditionNote.equals=" + DEFAULT_CONDITION_NOTE, "conditionNote.equals=" + UPDATED_CONDITION_NOTE);
    }

    @Test
    @Transactional
    void getAllEquipmentByConditionNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where conditionNote in
        defaultEquipmentFiltering(
            "conditionNote.in=" + DEFAULT_CONDITION_NOTE + "," + UPDATED_CONDITION_NOTE,
            "conditionNote.in=" + UPDATED_CONDITION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByConditionNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where conditionNote is not null
        defaultEquipmentFiltering("conditionNote.specified=true", "conditionNote.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByConditionNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where conditionNote contains
        defaultEquipmentFiltering("conditionNote.contains=" + DEFAULT_CONDITION_NOTE, "conditionNote.contains=" + UPDATED_CONDITION_NOTE);
    }

    @Test
    @Transactional
    void getAllEquipmentByConditionNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where conditionNote does not contain
        defaultEquipmentFiltering(
            "conditionNote.doesNotContain=" + UPDATED_CONDITION_NOTE,
            "conditionNote.doesNotContain=" + DEFAULT_CONDITION_NOTE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where status equals to
        defaultEquipmentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEquipmentByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where status in
        defaultEquipmentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEquipmentByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where status is not null
        defaultEquipmentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice equals to
        defaultEquipmentFiltering("purchasePrice.equals=" + DEFAULT_PURCHASE_PRICE, "purchasePrice.equals=" + UPDATED_PURCHASE_PRICE);
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice in
        defaultEquipmentFiltering(
            "purchasePrice.in=" + DEFAULT_PURCHASE_PRICE + "," + UPDATED_PURCHASE_PRICE,
            "purchasePrice.in=" + UPDATED_PURCHASE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice is not null
        defaultEquipmentFiltering("purchasePrice.specified=true", "purchasePrice.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice is greater than or equal to
        defaultEquipmentFiltering(
            "purchasePrice.greaterThanOrEqual=" + DEFAULT_PURCHASE_PRICE,
            "purchasePrice.greaterThanOrEqual=" + UPDATED_PURCHASE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice is less than or equal to
        defaultEquipmentFiltering(
            "purchasePrice.lessThanOrEqual=" + DEFAULT_PURCHASE_PRICE,
            "purchasePrice.lessThanOrEqual=" + SMALLER_PURCHASE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice is less than
        defaultEquipmentFiltering("purchasePrice.lessThan=" + UPDATED_PURCHASE_PRICE, "purchasePrice.lessThan=" + DEFAULT_PURCHASE_PRICE);
    }

    @Test
    @Transactional
    void getAllEquipmentByPurchasePriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where purchasePrice is greater than
        defaultEquipmentFiltering(
            "purchasePrice.greaterThan=" + SMALLER_PURCHASE_PRICE,
            "purchasePrice.greaterThan=" + DEFAULT_PURCHASE_PRICE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate equals to
        defaultEquipmentFiltering(
            "capitalizationDate.equals=" + DEFAULT_CAPITALIZATION_DATE,
            "capitalizationDate.equals=" + UPDATED_CAPITALIZATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate in
        defaultEquipmentFiltering(
            "capitalizationDate.in=" + DEFAULT_CAPITALIZATION_DATE + "," + UPDATED_CAPITALIZATION_DATE,
            "capitalizationDate.in=" + UPDATED_CAPITALIZATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate is not null
        defaultEquipmentFiltering("capitalizationDate.specified=true", "capitalizationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate is greater than or equal to
        defaultEquipmentFiltering(
            "capitalizationDate.greaterThanOrEqual=" + DEFAULT_CAPITALIZATION_DATE,
            "capitalizationDate.greaterThanOrEqual=" + UPDATED_CAPITALIZATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate is less than or equal to
        defaultEquipmentFiltering(
            "capitalizationDate.lessThanOrEqual=" + DEFAULT_CAPITALIZATION_DATE,
            "capitalizationDate.lessThanOrEqual=" + SMALLER_CAPITALIZATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate is less than
        defaultEquipmentFiltering(
            "capitalizationDate.lessThan=" + UPDATED_CAPITALIZATION_DATE,
            "capitalizationDate.lessThan=" + DEFAULT_CAPITALIZATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByCapitalizationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where capitalizationDate is greater than
        defaultEquipmentFiltering(
            "capitalizationDate.greaterThan=" + SMALLER_CAPITALIZATION_DATE,
            "capitalizationDate.greaterThan=" + DEFAULT_CAPITALIZATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths equals to
        defaultEquipmentFiltering(
            "depreciationMonths.equals=" + DEFAULT_DEPRECIATION_MONTHS,
            "depreciationMonths.equals=" + UPDATED_DEPRECIATION_MONTHS
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths in
        defaultEquipmentFiltering(
            "depreciationMonths.in=" + DEFAULT_DEPRECIATION_MONTHS + "," + UPDATED_DEPRECIATION_MONTHS,
            "depreciationMonths.in=" + UPDATED_DEPRECIATION_MONTHS
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths is not null
        defaultEquipmentFiltering("depreciationMonths.specified=true", "depreciationMonths.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths is greater than or equal to
        defaultEquipmentFiltering(
            "depreciationMonths.greaterThanOrEqual=" + DEFAULT_DEPRECIATION_MONTHS,
            "depreciationMonths.greaterThanOrEqual=" + UPDATED_DEPRECIATION_MONTHS
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths is less than or equal to
        defaultEquipmentFiltering(
            "depreciationMonths.lessThanOrEqual=" + DEFAULT_DEPRECIATION_MONTHS,
            "depreciationMonths.lessThanOrEqual=" + SMALLER_DEPRECIATION_MONTHS
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths is less than
        defaultEquipmentFiltering(
            "depreciationMonths.lessThan=" + UPDATED_DEPRECIATION_MONTHS,
            "depreciationMonths.lessThan=" + DEFAULT_DEPRECIATION_MONTHS
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByDepreciationMonthsIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where depreciationMonths is greater than
        defaultEquipmentFiltering(
            "depreciationMonths.greaterThan=" + SMALLER_DEPRECIATION_MONTHS,
            "depreciationMonths.greaterThan=" + DEFAULT_DEPRECIATION_MONTHS
        );
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue equals to
        defaultEquipmentFiltering("salvageValue.equals=" + DEFAULT_SALVAGE_VALUE, "salvageValue.equals=" + UPDATED_SALVAGE_VALUE);
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue in
        defaultEquipmentFiltering(
            "salvageValue.in=" + DEFAULT_SALVAGE_VALUE + "," + UPDATED_SALVAGE_VALUE,
            "salvageValue.in=" + UPDATED_SALVAGE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue is not null
        defaultEquipmentFiltering("salvageValue.specified=true", "salvageValue.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue is greater than or equal to
        defaultEquipmentFiltering(
            "salvageValue.greaterThanOrEqual=" + DEFAULT_SALVAGE_VALUE,
            "salvageValue.greaterThanOrEqual=" + UPDATED_SALVAGE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue is less than or equal to
        defaultEquipmentFiltering(
            "salvageValue.lessThanOrEqual=" + DEFAULT_SALVAGE_VALUE,
            "salvageValue.lessThanOrEqual=" + SMALLER_SALVAGE_VALUE
        );
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue is less than
        defaultEquipmentFiltering("salvageValue.lessThan=" + UPDATED_SALVAGE_VALUE, "salvageValue.lessThan=" + DEFAULT_SALVAGE_VALUE);
    }

    @Test
    @Transactional
    void getAllEquipmentBySalvageValueIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where salvageValue is greater than
        defaultEquipmentFiltering("salvageValue.greaterThan=" + SMALLER_SALVAGE_VALUE, "salvageValue.greaterThan=" + DEFAULT_SALVAGE_VALUE);
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot equals to
        defaultEquipmentFiltering(
            "bookValueSnapshot.equals=" + DEFAULT_BOOK_VALUE_SNAPSHOT,
            "bookValueSnapshot.equals=" + UPDATED_BOOK_VALUE_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot in
        defaultEquipmentFiltering(
            "bookValueSnapshot.in=" + DEFAULT_BOOK_VALUE_SNAPSHOT + "," + UPDATED_BOOK_VALUE_SNAPSHOT,
            "bookValueSnapshot.in=" + UPDATED_BOOK_VALUE_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot is not null
        defaultEquipmentFiltering("bookValueSnapshot.specified=true", "bookValueSnapshot.specified=false");
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot is greater than or equal to
        defaultEquipmentFiltering(
            "bookValueSnapshot.greaterThanOrEqual=" + DEFAULT_BOOK_VALUE_SNAPSHOT,
            "bookValueSnapshot.greaterThanOrEqual=" + UPDATED_BOOK_VALUE_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot is less than or equal to
        defaultEquipmentFiltering(
            "bookValueSnapshot.lessThanOrEqual=" + DEFAULT_BOOK_VALUE_SNAPSHOT,
            "bookValueSnapshot.lessThanOrEqual=" + SMALLER_BOOK_VALUE_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot is less than
        defaultEquipmentFiltering(
            "bookValueSnapshot.lessThan=" + UPDATED_BOOK_VALUE_SNAPSHOT,
            "bookValueSnapshot.lessThan=" + DEFAULT_BOOK_VALUE_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByBookValueSnapshotIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        // Get all the equipmentList where bookValueSnapshot is greater than
        defaultEquipmentFiltering(
            "bookValueSnapshot.greaterThan=" + SMALLER_BOOK_VALUE_SNAPSHOT,
            "bookValueSnapshot.greaterThan=" + DEFAULT_BOOK_VALUE_SNAPSHOT
        );
    }

    @Test
    @Transactional
    void getAllEquipmentByAssetItemIsEqualToSomething() throws Exception {
        AssetItem assetItem;
        if (TestUtil.findAll(em, AssetItem.class).isEmpty()) {
            equipmentRepository.saveAndFlush(equipment);
            assetItem = AssetItemResourceIT.createEntity();
        } else {
            assetItem = TestUtil.findAll(em, AssetItem.class).get(0);
        }
        em.persist(assetItem);
        em.flush();
        equipment.setAssetItem(assetItem);
        equipmentRepository.saveAndFlush(equipment);
        Long assetItemId = assetItem.getId();
        // Get all the equipmentList where assetItem equals to assetItemId
        defaultEquipmentShouldBeFound("assetItemId.equals=" + assetItemId);

        // Get all the equipmentList where assetItem equals to (assetItemId + 1)
        defaultEquipmentShouldNotBeFound("assetItemId.equals=" + (assetItemId + 1));
    }

    @Test
    @Transactional
    void getAllEquipmentBySupplierIsEqualToSomething() throws Exception {
        Supplier supplier;
        if (TestUtil.findAll(em, Supplier.class).isEmpty()) {
            equipmentRepository.saveAndFlush(equipment);
            supplier = SupplierResourceIT.createEntity();
        } else {
            supplier = TestUtil.findAll(em, Supplier.class).get(0);
        }
        em.persist(supplier);
        em.flush();
        equipment.setSupplier(supplier);
        equipmentRepository.saveAndFlush(equipment);
        Long supplierId = supplier.getId();
        // Get all the equipmentList where supplier equals to supplierId
        defaultEquipmentShouldBeFound("supplierId.equals=" + supplierId);

        // Get all the equipmentList where supplier equals to (supplierId + 1)
        defaultEquipmentShouldNotBeFound("supplierId.equals=" + (supplierId + 1));
    }

    private void defaultEquipmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEquipmentShouldBeFound(shouldBeFound);
        defaultEquipmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEquipmentShouldBeFound(String filter) throws Exception {
        restEquipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(equipment.getId().intValue())))
            .andExpect(jsonPath("$.[*].equipmentCode").value(hasItem(DEFAULT_EQUIPMENT_CODE)))
            .andExpect(jsonPath("$.[*].serial").value(hasItem(DEFAULT_SERIAL)))
            .andExpect(jsonPath("$.[*].conditionNote").value(hasItem(DEFAULT_CONDITION_NOTE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].purchasePrice").value(hasItem(sameNumber(DEFAULT_PURCHASE_PRICE))))
            .andExpect(jsonPath("$.[*].capitalizationDate").value(hasItem(DEFAULT_CAPITALIZATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].depreciationMonths").value(hasItem(DEFAULT_DEPRECIATION_MONTHS)))
            .andExpect(jsonPath("$.[*].salvageValue").value(hasItem(sameNumber(DEFAULT_SALVAGE_VALUE))))
            .andExpect(jsonPath("$.[*].bookValueSnapshot").value(hasItem(sameNumber(DEFAULT_BOOK_VALUE_SNAPSHOT))));

        // Check, that the count call also returns 1
        restEquipmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEquipmentShouldNotBeFound(String filter) throws Exception {
        restEquipmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEquipmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEquipment() throws Exception {
        // Get the equipment
        restEquipmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEquipment() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipment
        Equipment updatedEquipment = equipmentRepository.findById(equipment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEquipment are not directly saved in db
        em.detach(updatedEquipment);
        updatedEquipment
            .equipmentCode(UPDATED_EQUIPMENT_CODE)
            .serial(UPDATED_SERIAL)
            .conditionNote(UPDATED_CONDITION_NOTE)
            .status(UPDATED_STATUS)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .capitalizationDate(UPDATED_CAPITALIZATION_DATE)
            .depreciationMonths(UPDATED_DEPRECIATION_MONTHS)
            .salvageValue(UPDATED_SALVAGE_VALUE)
            .bookValueSnapshot(UPDATED_BOOK_VALUE_SNAPSHOT);
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(updatedEquipment);

        restEquipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEquipmentToMatchAllProperties(updatedEquipment);
    }

    @Test
    @Transactional
    void putNonExistingEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, equipmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(equipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(equipmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEquipmentWithPatch() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipment using partial update
        Equipment partialUpdatedEquipment = new Equipment();
        partialUpdatedEquipment.setId(equipment.getId());

        partialUpdatedEquipment
            .serial(UPDATED_SERIAL)
            .status(UPDATED_STATUS)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .depreciationMonths(UPDATED_DEPRECIATION_MONTHS)
            .salvageValue(UPDATED_SALVAGE_VALUE)
            .bookValueSnapshot(UPDATED_BOOK_VALUE_SNAPSHOT);

        restEquipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEquipment))
            )
            .andExpect(status().isOk());

        // Validate the Equipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEquipment, equipment),
            getPersistedEquipment(equipment)
        );
    }

    @Test
    @Transactional
    void fullUpdateEquipmentWithPatch() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the equipment using partial update
        Equipment partialUpdatedEquipment = new Equipment();
        partialUpdatedEquipment.setId(equipment.getId());

        partialUpdatedEquipment
            .equipmentCode(UPDATED_EQUIPMENT_CODE)
            .serial(UPDATED_SERIAL)
            .conditionNote(UPDATED_CONDITION_NOTE)
            .status(UPDATED_STATUS)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .capitalizationDate(UPDATED_CAPITALIZATION_DATE)
            .depreciationMonths(UPDATED_DEPRECIATION_MONTHS)
            .salvageValue(UPDATED_SALVAGE_VALUE)
            .bookValueSnapshot(UPDATED_BOOK_VALUE_SNAPSHOT);

        restEquipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEquipment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEquipment))
            )
            .andExpect(status().isOk());

        // Validate the Equipment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEquipmentUpdatableFieldsEquals(partialUpdatedEquipment, getPersistedEquipment(partialUpdatedEquipment));
    }

    @Test
    @Transactional
    void patchNonExistingEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEquipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, equipmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(equipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(equipmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEquipment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        equipment.setId(longCount.incrementAndGet());

        // Create the Equipment
        EquipmentDTO equipmentDTO = equipmentMapper.toDto(equipment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEquipmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(equipmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Equipment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEquipment() throws Exception {
        // Initialize the database
        insertedEquipment = equipmentRepository.saveAndFlush(equipment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the equipment
        restEquipmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, equipment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return equipmentRepository.count();
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

    protected Equipment getPersistedEquipment(Equipment equipment) {
        return equipmentRepository.findById(equipment.getId()).orElseThrow();
    }

    protected void assertPersistedEquipmentToMatchAllProperties(Equipment expectedEquipment) {
        assertEquipmentAllPropertiesEquals(expectedEquipment, getPersistedEquipment(expectedEquipment));
    }

    protected void assertPersistedEquipmentToMatchUpdatableProperties(Equipment expectedEquipment) {
        assertEquipmentAllUpdatablePropertiesEquals(expectedEquipment, getPersistedEquipment(expectedEquipment));
    }
}

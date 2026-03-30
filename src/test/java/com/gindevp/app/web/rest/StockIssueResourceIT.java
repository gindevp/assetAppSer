package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.StockIssueAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.Department;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Location;
import com.gindevp.app.domain.StockIssue;
import com.gindevp.app.domain.StockIssue;
import com.gindevp.app.domain.enumeration.AssigneeType;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.repository.StockIssueRepository;
import com.gindevp.app.service.StockIssueService;
import com.gindevp.app.service.dto.StockIssueDTO;
import com.gindevp.app.service.mapper.StockIssueMapper;
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
 * Integration tests for the {@link StockIssueResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StockIssueResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ISSUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ISSUE_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_ISSUE_DATE = LocalDate.ofEpochDay(-1L);

    private static final DocumentStatus DEFAULT_STATUS = DocumentStatus.DRAFT;
    private static final DocumentStatus UPDATED_STATUS = DocumentStatus.CONFIRMED;

    private static final AssigneeType DEFAULT_ASSIGNEE_TYPE = AssigneeType.EMPLOYEE;
    private static final AssigneeType UPDATED_ASSIGNEE_TYPE = AssigneeType.DEPARTMENT;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stock-issues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private StockIssueRepository stockIssueRepository;

    @Mock
    private StockIssueRepository stockIssueRepositoryMock;

    @Autowired
    private StockIssueMapper stockIssueMapper;

    @Mock
    private StockIssueService stockIssueServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStockIssueMockMvc;

    private StockIssue stockIssue;

    private StockIssue insertedStockIssue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockIssue createEntity() {
        return new StockIssue()
            .code(DEFAULT_CODE)
            .issueDate(DEFAULT_ISSUE_DATE)
            .status(DEFAULT_STATUS)
            .assigneeType(DEFAULT_ASSIGNEE_TYPE)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static StockIssue createUpdatedEntity() {
        return new StockIssue()
            .code(UPDATED_CODE)
            .issueDate(UPDATED_ISSUE_DATE)
            .status(UPDATED_STATUS)
            .assigneeType(UPDATED_ASSIGNEE_TYPE)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        stockIssue = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedStockIssue != null) {
            stockIssueRepository.delete(insertedStockIssue);
            insertedStockIssue = null;
        }
    }

    @Test
    @Transactional
    void createStockIssue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);
        var returnedStockIssueDTO = om.readValue(
            restStockIssueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            StockIssueDTO.class
        );

        // Validate the StockIssue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedStockIssue = stockIssueMapper.toEntity(returnedStockIssueDTO);
        assertStockIssueUpdatableFieldsEquals(returnedStockIssue, getPersistedStockIssue(returnedStockIssue));

        insertedStockIssue = returnedStockIssue;
    }

    @Test
    @Transactional
    void createStockIssueWithExistingId() throws Exception {
        // Create the StockIssue with an existing ID
        stockIssue.setId(1L);
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restStockIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockIssue.setCode(null);

        // Create the StockIssue, which fails.
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        restStockIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIssueDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockIssue.setIssueDate(null);

        // Create the StockIssue, which fails.
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        restStockIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockIssue.setStatus(null);

        // Create the StockIssue, which fails.
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        restStockIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAssigneeTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        stockIssue.setAssigneeType(null);

        // Create the StockIssue, which fails.
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        restStockIssueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllStockIssues() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList
        restStockIssueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockIssue.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(DEFAULT_ISSUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].assigneeType").value(hasItem(DEFAULT_ASSIGNEE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockIssuesWithEagerRelationshipsIsEnabled() throws Exception {
        when(stockIssueServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockIssueMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(stockIssueServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStockIssuesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(stockIssueServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStockIssueMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(stockIssueRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStockIssue() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get the stockIssue
        restStockIssueMockMvc
            .perform(get(ENTITY_API_URL_ID, stockIssue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(stockIssue.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.issueDate").value(DEFAULT_ISSUE_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.assigneeType").value(DEFAULT_ASSIGNEE_TYPE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getStockIssuesByIdFiltering() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        Long id = stockIssue.getId();

        defaultStockIssueFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultStockIssueFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultStockIssueFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllStockIssuesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where code equals to
        defaultStockIssueFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where code in
        defaultStockIssueFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where code is not null
        defaultStockIssueFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllStockIssuesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where code contains
        defaultStockIssueFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where code does not contain
        defaultStockIssueFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate equals to
        defaultStockIssueFiltering("issueDate.equals=" + DEFAULT_ISSUE_DATE, "issueDate.equals=" + UPDATED_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate in
        defaultStockIssueFiltering("issueDate.in=" + DEFAULT_ISSUE_DATE + "," + UPDATED_ISSUE_DATE, "issueDate.in=" + UPDATED_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate is not null
        defaultStockIssueFiltering("issueDate.specified=true", "issueDate.specified=false");
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate is greater than or equal to
        defaultStockIssueFiltering(
            "issueDate.greaterThanOrEqual=" + DEFAULT_ISSUE_DATE,
            "issueDate.greaterThanOrEqual=" + UPDATED_ISSUE_DATE
        );
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate is less than or equal to
        defaultStockIssueFiltering("issueDate.lessThanOrEqual=" + DEFAULT_ISSUE_DATE, "issueDate.lessThanOrEqual=" + SMALLER_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate is less than
        defaultStockIssueFiltering("issueDate.lessThan=" + UPDATED_ISSUE_DATE, "issueDate.lessThan=" + DEFAULT_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByIssueDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where issueDate is greater than
        defaultStockIssueFiltering("issueDate.greaterThan=" + SMALLER_ISSUE_DATE, "issueDate.greaterThan=" + DEFAULT_ISSUE_DATE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where status equals to
        defaultStockIssueFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockIssuesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where status in
        defaultStockIssueFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllStockIssuesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where status is not null
        defaultStockIssueFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllStockIssuesByAssigneeTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where assigneeType equals to
        defaultStockIssueFiltering("assigneeType.equals=" + DEFAULT_ASSIGNEE_TYPE, "assigneeType.equals=" + UPDATED_ASSIGNEE_TYPE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByAssigneeTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where assigneeType in
        defaultStockIssueFiltering(
            "assigneeType.in=" + DEFAULT_ASSIGNEE_TYPE + "," + UPDATED_ASSIGNEE_TYPE,
            "assigneeType.in=" + UPDATED_ASSIGNEE_TYPE
        );
    }

    @Test
    @Transactional
    void getAllStockIssuesByAssigneeTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where assigneeType is not null
        defaultStockIssueFiltering("assigneeType.specified=true", "assigneeType.specified=false");
    }

    @Test
    @Transactional
    void getAllStockIssuesByNoteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where note equals to
        defaultStockIssueFiltering("note.equals=" + DEFAULT_NOTE, "note.equals=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByNoteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where note in
        defaultStockIssueFiltering("note.in=" + DEFAULT_NOTE + "," + UPDATED_NOTE, "note.in=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByNoteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where note is not null
        defaultStockIssueFiltering("note.specified=true", "note.specified=false");
    }

    @Test
    @Transactional
    void getAllStockIssuesByNoteContainsSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where note contains
        defaultStockIssueFiltering("note.contains=" + DEFAULT_NOTE, "note.contains=" + UPDATED_NOTE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByNoteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        // Get all the stockIssueList where note does not contain
        defaultStockIssueFiltering("note.doesNotContain=" + UPDATED_NOTE, "note.doesNotContain=" + DEFAULT_NOTE);
    }

    @Test
    @Transactional
    void getAllStockIssuesByEmployeeIsEqualToSomething() throws Exception {
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            stockIssueRepository.saveAndFlush(stockIssue);
            employee = EmployeeResourceIT.createEntity();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        em.persist(employee);
        em.flush();
        stockIssue.setEmployee(employee);
        stockIssueRepository.saveAndFlush(stockIssue);
        Long employeeId = employee.getId();
        // Get all the stockIssueList where employee equals to employeeId
        defaultStockIssueShouldBeFound("employeeId.equals=" + employeeId);

        // Get all the stockIssueList where employee equals to (employeeId + 1)
        defaultStockIssueShouldNotBeFound("employeeId.equals=" + (employeeId + 1));
    }

    @Test
    @Transactional
    void getAllStockIssuesByDepartmentIsEqualToSomething() throws Exception {
        Department department;
        if (TestUtil.findAll(em, Department.class).isEmpty()) {
            stockIssueRepository.saveAndFlush(stockIssue);
            department = DepartmentResourceIT.createEntity();
        } else {
            department = TestUtil.findAll(em, Department.class).get(0);
        }
        em.persist(department);
        em.flush();
        stockIssue.setDepartment(department);
        stockIssueRepository.saveAndFlush(stockIssue);
        Long departmentId = department.getId();
        // Get all the stockIssueList where department equals to departmentId
        defaultStockIssueShouldBeFound("departmentId.equals=" + departmentId);

        // Get all the stockIssueList where department equals to (departmentId + 1)
        defaultStockIssueShouldNotBeFound("departmentId.equals=" + (departmentId + 1));
    }

    @Test
    @Transactional
    void getAllStockIssuesByLocationIsEqualToSomething() throws Exception {
        Location location;
        if (TestUtil.findAll(em, Location.class).isEmpty()) {
            stockIssueRepository.saveAndFlush(stockIssue);
            location = LocationResourceIT.createEntity();
        } else {
            location = TestUtil.findAll(em, Location.class).get(0);
        }
        em.persist(location);
        em.flush();
        stockIssue.setLocation(location);
        stockIssueRepository.saveAndFlush(stockIssue);
        Long locationId = location.getId();
        // Get all the stockIssueList where location equals to locationId
        defaultStockIssueShouldBeFound("locationId.equals=" + locationId);

        // Get all the stockIssueList where location equals to (locationId + 1)
        defaultStockIssueShouldNotBeFound("locationId.equals=" + (locationId + 1));
    }

    private void defaultStockIssueFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultStockIssueShouldBeFound(shouldBeFound);
        defaultStockIssueShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultStockIssueShouldBeFound(String filter) throws Exception {
        restStockIssueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(stockIssue.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].issueDate").value(hasItem(DEFAULT_ISSUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].assigneeType").value(hasItem(DEFAULT_ASSIGNEE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));

        // Check, that the count call also returns 1
        restStockIssueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultStockIssueShouldNotBeFound(String filter) throws Exception {
        restStockIssueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restStockIssueMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingStockIssue() throws Exception {
        // Get the stockIssue
        restStockIssueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStockIssue() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockIssue
        StockIssue updatedStockIssue = stockIssueRepository.findById(stockIssue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedStockIssue are not directly saved in db
        em.detach(updatedStockIssue);
        updatedStockIssue
            .code(UPDATED_CODE)
            .issueDate(UPDATED_ISSUE_DATE)
            .status(UPDATED_STATUS)
            .assigneeType(UPDATED_ASSIGNEE_TYPE)
            .note(UPDATED_NOTE);
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(updatedStockIssue);

        restStockIssueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockIssueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockIssueDTO))
            )
            .andExpect(status().isOk());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedStockIssueToMatchAllProperties(updatedStockIssue);
    }

    @Test
    @Transactional
    void putNonExistingStockIssue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssue.setId(longCount.incrementAndGet());

        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockIssueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, stockIssueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockIssueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchStockIssue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssue.setId(longCount.incrementAndGet());

        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(stockIssueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStockIssue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssue.setId(longCount.incrementAndGet());

        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateStockIssueWithPatch() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockIssue using partial update
        StockIssue partialUpdatedStockIssue = new StockIssue();
        partialUpdatedStockIssue.setId(stockIssue.getId());

        partialUpdatedStockIssue.code(UPDATED_CODE).status(UPDATED_STATUS);

        restStockIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockIssue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockIssue))
            )
            .andExpect(status().isOk());

        // Validate the StockIssue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockIssueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedStockIssue, stockIssue),
            getPersistedStockIssue(stockIssue)
        );
    }

    @Test
    @Transactional
    void fullUpdateStockIssueWithPatch() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the stockIssue using partial update
        StockIssue partialUpdatedStockIssue = new StockIssue();
        partialUpdatedStockIssue.setId(stockIssue.getId());

        partialUpdatedStockIssue
            .code(UPDATED_CODE)
            .issueDate(UPDATED_ISSUE_DATE)
            .status(UPDATED_STATUS)
            .assigneeType(UPDATED_ASSIGNEE_TYPE)
            .note(UPDATED_NOTE);

        restStockIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStockIssue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedStockIssue))
            )
            .andExpect(status().isOk());

        // Validate the StockIssue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertStockIssueUpdatableFieldsEquals(partialUpdatedStockIssue, getPersistedStockIssue(partialUpdatedStockIssue));
    }

    @Test
    @Transactional
    void patchNonExistingStockIssue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssue.setId(longCount.incrementAndGet());

        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStockIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, stockIssueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockIssueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStockIssue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssue.setId(longCount.incrementAndGet());

        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(stockIssueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStockIssue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        stockIssue.setId(longCount.incrementAndGet());

        // Create the StockIssue
        StockIssueDTO stockIssueDTO = stockIssueMapper.toDto(stockIssue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStockIssueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(stockIssueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the StockIssue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteStockIssue() throws Exception {
        // Initialize the database
        insertedStockIssue = stockIssueRepository.saveAndFlush(stockIssue);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the stockIssue
        restStockIssueMockMvc
            .perform(delete(ENTITY_API_URL_ID, stockIssue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return stockIssueRepository.count();
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

    protected StockIssue getPersistedStockIssue(StockIssue stockIssue) {
        return stockIssueRepository.findById(stockIssue.getId()).orElseThrow();
    }

    protected void assertPersistedStockIssueToMatchAllProperties(StockIssue expectedStockIssue) {
        assertStockIssueAllPropertiesEquals(expectedStockIssue, getPersistedStockIssue(expectedStockIssue));
    }

    protected void assertPersistedStockIssueToMatchUpdatableProperties(StockIssue expectedStockIssue) {
        assertStockIssueAllUpdatablePropertiesEquals(expectedStockIssue, getPersistedStockIssue(expectedStockIssue));
    }
}

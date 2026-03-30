package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.ReturnRequestLineAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.ReturnRequestLine;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.repository.ReturnRequestLineRepository;
import com.gindevp.app.service.ReturnRequestLineService;
import com.gindevp.app.service.dto.ReturnRequestLineDTO;
import com.gindevp.app.service.mapper.ReturnRequestLineMapper;
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
 * Integration tests for the {@link ReturnRequestLineResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ReturnRequestLineResourceIT {

    private static final Integer DEFAULT_LINE_NO = 1;
    private static final Integer UPDATED_LINE_NO = 2;

    private static final AssetManagementType DEFAULT_LINE_TYPE = AssetManagementType.DEVICE;
    private static final AssetManagementType UPDATED_LINE_TYPE = AssetManagementType.CONSUMABLE;

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final Boolean DEFAULT_SELECTED = false;
    private static final Boolean UPDATED_SELECTED = true;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/return-request-lines";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ReturnRequestLineRepository returnRequestLineRepository;

    @Mock
    private ReturnRequestLineRepository returnRequestLineRepositoryMock;

    @Autowired
    private ReturnRequestLineMapper returnRequestLineMapper;

    @Mock
    private ReturnRequestLineService returnRequestLineServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restReturnRequestLineMockMvc;

    private ReturnRequestLine returnRequestLine;

    private ReturnRequestLine insertedReturnRequestLine;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnRequestLine createEntity() {
        return new ReturnRequestLine()
            .lineNo(DEFAULT_LINE_NO)
            .lineType(DEFAULT_LINE_TYPE)
            .quantity(DEFAULT_QUANTITY)
            .selected(DEFAULT_SELECTED)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ReturnRequestLine createUpdatedEntity() {
        return new ReturnRequestLine()
            .lineNo(UPDATED_LINE_NO)
            .lineType(UPDATED_LINE_TYPE)
            .quantity(UPDATED_QUANTITY)
            .selected(UPDATED_SELECTED)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        returnRequestLine = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedReturnRequestLine != null) {
            returnRequestLineRepository.delete(insertedReturnRequestLine);
            insertedReturnRequestLine = null;
        }
    }

    @Test
    @Transactional
    void createReturnRequestLine() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);
        var returnedReturnRequestLineDTO = om.readValue(
            restReturnRequestLineMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestLineDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ReturnRequestLineDTO.class
        );

        // Validate the ReturnRequestLine in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedReturnRequestLine = returnRequestLineMapper.toEntity(returnedReturnRequestLineDTO);
        assertReturnRequestLineUpdatableFieldsEquals(returnedReturnRequestLine, getPersistedReturnRequestLine(returnedReturnRequestLine));

        insertedReturnRequestLine = returnedReturnRequestLine;
    }

    @Test
    @Transactional
    void createReturnRequestLineWithExistingId() throws Exception {
        // Create the ReturnRequestLine with an existing ID
        returnRequestLine.setId(1L);
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restReturnRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestLineDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLineNoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnRequestLine.setLineNo(null);

        // Create the ReturnRequestLine, which fails.
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        restReturnRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLineTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnRequestLine.setLineType(null);

        // Create the ReturnRequestLine, which fails.
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        restReturnRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSelectedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        returnRequestLine.setSelected(null);

        // Create the ReturnRequestLine, which fails.
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        restReturnRequestLineMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestLineDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllReturnRequestLines() throws Exception {
        // Initialize the database
        insertedReturnRequestLine = returnRequestLineRepository.saveAndFlush(returnRequestLine);

        // Get all the returnRequestLineList
        restReturnRequestLineMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(returnRequestLine.getId().intValue())))
            .andExpect(jsonPath("$.[*].lineNo").value(hasItem(DEFAULT_LINE_NO)))
            .andExpect(jsonPath("$.[*].lineType").value(hasItem(DEFAULT_LINE_TYPE.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].selected").value(hasItem(DEFAULT_SELECTED)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnRequestLinesWithEagerRelationshipsIsEnabled() throws Exception {
        when(returnRequestLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnRequestLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(returnRequestLineServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllReturnRequestLinesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(returnRequestLineServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restReturnRequestLineMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(returnRequestLineRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getReturnRequestLine() throws Exception {
        // Initialize the database
        insertedReturnRequestLine = returnRequestLineRepository.saveAndFlush(returnRequestLine);

        // Get the returnRequestLine
        restReturnRequestLineMockMvc
            .perform(get(ENTITY_API_URL_ID, returnRequestLine.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(returnRequestLine.getId().intValue()))
            .andExpect(jsonPath("$.lineNo").value(DEFAULT_LINE_NO))
            .andExpect(jsonPath("$.lineType").value(DEFAULT_LINE_TYPE.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.selected").value(DEFAULT_SELECTED))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingReturnRequestLine() throws Exception {
        // Get the returnRequestLine
        restReturnRequestLineMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingReturnRequestLine() throws Exception {
        // Initialize the database
        insertedReturnRequestLine = returnRequestLineRepository.saveAndFlush(returnRequestLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnRequestLine
        ReturnRequestLine updatedReturnRequestLine = returnRequestLineRepository.findById(returnRequestLine.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedReturnRequestLine are not directly saved in db
        em.detach(updatedReturnRequestLine);
        updatedReturnRequestLine
            .lineNo(UPDATED_LINE_NO)
            .lineType(UPDATED_LINE_TYPE)
            .quantity(UPDATED_QUANTITY)
            .selected(UPDATED_SELECTED)
            .note(UPDATED_NOTE);
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(updatedReturnRequestLine);

        restReturnRequestLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnRequestLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnRequestLineDTO))
            )
            .andExpect(status().isOk());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedReturnRequestLineToMatchAllProperties(updatedReturnRequestLine);
    }

    @Test
    @Transactional
    void putNonExistingReturnRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequestLine.setId(longCount.incrementAndGet());

        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnRequestLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, returnRequestLineDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchReturnRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequestLine.setId(longCount.incrementAndGet());

        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestLineMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(returnRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamReturnRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequestLine.setId(longCount.incrementAndGet());

        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestLineMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(returnRequestLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateReturnRequestLineWithPatch() throws Exception {
        // Initialize the database
        insertedReturnRequestLine = returnRequestLineRepository.saveAndFlush(returnRequestLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnRequestLine using partial update
        ReturnRequestLine partialUpdatedReturnRequestLine = new ReturnRequestLine();
        partialUpdatedReturnRequestLine.setId(returnRequestLine.getId());

        partialUpdatedReturnRequestLine.lineType(UPDATED_LINE_TYPE).quantity(UPDATED_QUANTITY).selected(UPDATED_SELECTED);

        restReturnRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnRequestLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnRequestLine))
            )
            .andExpect(status().isOk());

        // Validate the ReturnRequestLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnRequestLineUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedReturnRequestLine, returnRequestLine),
            getPersistedReturnRequestLine(returnRequestLine)
        );
    }

    @Test
    @Transactional
    void fullUpdateReturnRequestLineWithPatch() throws Exception {
        // Initialize the database
        insertedReturnRequestLine = returnRequestLineRepository.saveAndFlush(returnRequestLine);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the returnRequestLine using partial update
        ReturnRequestLine partialUpdatedReturnRequestLine = new ReturnRequestLine();
        partialUpdatedReturnRequestLine.setId(returnRequestLine.getId());

        partialUpdatedReturnRequestLine
            .lineNo(UPDATED_LINE_NO)
            .lineType(UPDATED_LINE_TYPE)
            .quantity(UPDATED_QUANTITY)
            .selected(UPDATED_SELECTED)
            .note(UPDATED_NOTE);

        restReturnRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedReturnRequestLine.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedReturnRequestLine))
            )
            .andExpect(status().isOk());

        // Validate the ReturnRequestLine in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertReturnRequestLineUpdatableFieldsEquals(
            partialUpdatedReturnRequestLine,
            getPersistedReturnRequestLine(partialUpdatedReturnRequestLine)
        );
    }

    @Test
    @Transactional
    void patchNonExistingReturnRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequestLine.setId(longCount.incrementAndGet());

        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restReturnRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, returnRequestLineDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchReturnRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequestLine.setId(longCount.incrementAndGet());

        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestLineMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(returnRequestLineDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamReturnRequestLine() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        returnRequestLine.setId(longCount.incrementAndGet());

        // Create the ReturnRequestLine
        ReturnRequestLineDTO returnRequestLineDTO = returnRequestLineMapper.toDto(returnRequestLine);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restReturnRequestLineMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(returnRequestLineDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ReturnRequestLine in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteReturnRequestLine() throws Exception {
        // Initialize the database
        insertedReturnRequestLine = returnRequestLineRepository.saveAndFlush(returnRequestLine);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the returnRequestLine
        restReturnRequestLineMockMvc
            .perform(delete(ENTITY_API_URL_ID, returnRequestLine.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return returnRequestLineRepository.count();
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

    protected ReturnRequestLine getPersistedReturnRequestLine(ReturnRequestLine returnRequestLine) {
        return returnRequestLineRepository.findById(returnRequestLine.getId()).orElseThrow();
    }

    protected void assertPersistedReturnRequestLineToMatchAllProperties(ReturnRequestLine expectedReturnRequestLine) {
        assertReturnRequestLineAllPropertiesEquals(expectedReturnRequestLine, getPersistedReturnRequestLine(expectedReturnRequestLine));
    }

    protected void assertPersistedReturnRequestLineToMatchUpdatableProperties(ReturnRequestLine expectedReturnRequestLine) {
        assertReturnRequestLineAllUpdatablePropertiesEquals(
            expectedReturnRequestLine,
            getPersistedReturnRequestLine(expectedReturnRequestLine)
        );
    }
}

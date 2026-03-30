package com.gindevp.app.web.rest;

import static com.gindevp.app.domain.ConsumableStockAsserts.*;
import static com.gindevp.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gindevp.app.IntegrationTest;
import com.gindevp.app.domain.ConsumableStock;
import com.gindevp.app.repository.ConsumableStockRepository;
import com.gindevp.app.service.ConsumableStockService;
import com.gindevp.app.service.dto.ConsumableStockDTO;
import com.gindevp.app.service.mapper.ConsumableStockMapper;
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
 * Integration tests for the {@link ConsumableStockResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ConsumableStockResourceIT {

    private static final Integer DEFAULT_QUANTITY_ON_HAND = 0;
    private static final Integer UPDATED_QUANTITY_ON_HAND = 1;

    private static final Integer DEFAULT_QUANTITY_ISSUED = 0;
    private static final Integer UPDATED_QUANTITY_ISSUED = 1;

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/consumable-stocks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ConsumableStockRepository consumableStockRepository;

    @Mock
    private ConsumableStockRepository consumableStockRepositoryMock;

    @Autowired
    private ConsumableStockMapper consumableStockMapper;

    @Mock
    private ConsumableStockService consumableStockServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConsumableStockMockMvc;

    private ConsumableStock consumableStock;

    private ConsumableStock insertedConsumableStock;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConsumableStock createEntity() {
        return new ConsumableStock().quantityOnHand(DEFAULT_QUANTITY_ON_HAND).quantityIssued(DEFAULT_QUANTITY_ISSUED).note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ConsumableStock createUpdatedEntity() {
        return new ConsumableStock().quantityOnHand(UPDATED_QUANTITY_ON_HAND).quantityIssued(UPDATED_QUANTITY_ISSUED).note(UPDATED_NOTE);
    }

    @BeforeEach
    public void initTest() {
        consumableStock = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedConsumableStock != null) {
            consumableStockRepository.delete(insertedConsumableStock);
            insertedConsumableStock = null;
        }
    }

    @Test
    @Transactional
    void createConsumableStock() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);
        var returnedConsumableStockDTO = om.readValue(
            restConsumableStockMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableStockDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ConsumableStockDTO.class
        );

        // Validate the ConsumableStock in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedConsumableStock = consumableStockMapper.toEntity(returnedConsumableStockDTO);
        assertConsumableStockUpdatableFieldsEquals(returnedConsumableStock, getPersistedConsumableStock(returnedConsumableStock));

        insertedConsumableStock = returnedConsumableStock;
    }

    @Test
    @Transactional
    void createConsumableStockWithExistingId() throws Exception {
        // Create the ConsumableStock with an existing ID
        consumableStock.setId(1L);
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConsumableStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableStockDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityOnHandIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        consumableStock.setQuantityOnHand(null);

        // Create the ConsumableStock, which fails.
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        restConsumableStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkQuantityIssuedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        consumableStock.setQuantityIssued(null);

        // Create the ConsumableStock, which fails.
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        restConsumableStockMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableStockDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllConsumableStocks() throws Exception {
        // Initialize the database
        insertedConsumableStock = consumableStockRepository.saveAndFlush(consumableStock);

        // Get all the consumableStockList
        restConsumableStockMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(consumableStock.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantityOnHand").value(hasItem(DEFAULT_QUANTITY_ON_HAND)))
            .andExpect(jsonPath("$.[*].quantityIssued").value(hasItem(DEFAULT_QUANTITY_ISSUED)))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllConsumableStocksWithEagerRelationshipsIsEnabled() throws Exception {
        when(consumableStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restConsumableStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(consumableStockServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllConsumableStocksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(consumableStockServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restConsumableStockMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(consumableStockRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getConsumableStock() throws Exception {
        // Initialize the database
        insertedConsumableStock = consumableStockRepository.saveAndFlush(consumableStock);

        // Get the consumableStock
        restConsumableStockMockMvc
            .perform(get(ENTITY_API_URL_ID, consumableStock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(consumableStock.getId().intValue()))
            .andExpect(jsonPath("$.quantityOnHand").value(DEFAULT_QUANTITY_ON_HAND))
            .andExpect(jsonPath("$.quantityIssued").value(DEFAULT_QUANTITY_ISSUED))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingConsumableStock() throws Exception {
        // Get the consumableStock
        restConsumableStockMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConsumableStock() throws Exception {
        // Initialize the database
        insertedConsumableStock = consumableStockRepository.saveAndFlush(consumableStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consumableStock
        ConsumableStock updatedConsumableStock = consumableStockRepository.findById(consumableStock.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedConsumableStock are not directly saved in db
        em.detach(updatedConsumableStock);
        updatedConsumableStock.quantityOnHand(UPDATED_QUANTITY_ON_HAND).quantityIssued(UPDATED_QUANTITY_ISSUED).note(UPDATED_NOTE);
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(updatedConsumableStock);

        restConsumableStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consumableStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consumableStockDTO))
            )
            .andExpect(status().isOk());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedConsumableStockToMatchAllProperties(updatedConsumableStock);
    }

    @Test
    @Transactional
    void putNonExistingConsumableStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableStock.setId(longCount.incrementAndGet());

        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsumableStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, consumableStockDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consumableStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConsumableStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableStock.setId(longCount.incrementAndGet());

        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableStockMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(consumableStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConsumableStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableStock.setId(longCount.incrementAndGet());

        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableStockMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(consumableStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConsumableStockWithPatch() throws Exception {
        // Initialize the database
        insertedConsumableStock = consumableStockRepository.saveAndFlush(consumableStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consumableStock using partial update
        ConsumableStock partialUpdatedConsumableStock = new ConsumableStock();
        partialUpdatedConsumableStock.setId(consumableStock.getId());

        partialUpdatedConsumableStock.quantityIssued(UPDATED_QUANTITY_ISSUED);

        restConsumableStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsumableStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsumableStock))
            )
            .andExpect(status().isOk());

        // Validate the ConsumableStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsumableStockUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedConsumableStock, consumableStock),
            getPersistedConsumableStock(consumableStock)
        );
    }

    @Test
    @Transactional
    void fullUpdateConsumableStockWithPatch() throws Exception {
        // Initialize the database
        insertedConsumableStock = consumableStockRepository.saveAndFlush(consumableStock);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the consumableStock using partial update
        ConsumableStock partialUpdatedConsumableStock = new ConsumableStock();
        partialUpdatedConsumableStock.setId(consumableStock.getId());

        partialUpdatedConsumableStock.quantityOnHand(UPDATED_QUANTITY_ON_HAND).quantityIssued(UPDATED_QUANTITY_ISSUED).note(UPDATED_NOTE);

        restConsumableStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConsumableStock.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedConsumableStock))
            )
            .andExpect(status().isOk());

        // Validate the ConsumableStock in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertConsumableStockUpdatableFieldsEquals(
            partialUpdatedConsumableStock,
            getPersistedConsumableStock(partialUpdatedConsumableStock)
        );
    }

    @Test
    @Transactional
    void patchNonExistingConsumableStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableStock.setId(longCount.incrementAndGet());

        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConsumableStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, consumableStockDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consumableStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConsumableStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableStock.setId(longCount.incrementAndGet());

        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableStockMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(consumableStockDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConsumableStock() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        consumableStock.setId(longCount.incrementAndGet());

        // Create the ConsumableStock
        ConsumableStockDTO consumableStockDTO = consumableStockMapper.toDto(consumableStock);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConsumableStockMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(consumableStockDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ConsumableStock in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConsumableStock() throws Exception {
        // Initialize the database
        insertedConsumableStock = consumableStockRepository.saveAndFlush(consumableStock);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the consumableStock
        restConsumableStockMockMvc
            .perform(delete(ENTITY_API_URL_ID, consumableStock.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return consumableStockRepository.count();
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

    protected ConsumableStock getPersistedConsumableStock(ConsumableStock consumableStock) {
        return consumableStockRepository.findById(consumableStock.getId()).orElseThrow();
    }

    protected void assertPersistedConsumableStockToMatchAllProperties(ConsumableStock expectedConsumableStock) {
        assertConsumableStockAllPropertiesEquals(expectedConsumableStock, getPersistedConsumableStock(expectedConsumableStock));
    }

    protected void assertPersistedConsumableStockToMatchUpdatableProperties(ConsumableStock expectedConsumableStock) {
        assertConsumableStockAllUpdatablePropertiesEquals(expectedConsumableStock, getPersistedConsumableStock(expectedConsumableStock));
    }
}

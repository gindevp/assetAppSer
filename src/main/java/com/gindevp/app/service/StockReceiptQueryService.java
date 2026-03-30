package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.StockReceipt;
import com.gindevp.app.repository.StockReceiptRepository;
import com.gindevp.app.service.criteria.StockReceiptCriteria;
import com.gindevp.app.service.dto.StockReceiptDTO;
import com.gindevp.app.service.mapper.StockReceiptMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StockReceipt} entities in the database.
 * The main input is a {@link StockReceiptCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StockReceiptDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockReceiptQueryService extends QueryService<StockReceipt> {

    private static final Logger LOG = LoggerFactory.getLogger(StockReceiptQueryService.class);

    private final StockReceiptRepository stockReceiptRepository;

    private final StockReceiptMapper stockReceiptMapper;

    public StockReceiptQueryService(StockReceiptRepository stockReceiptRepository, StockReceiptMapper stockReceiptMapper) {
        this.stockReceiptRepository = stockReceiptRepository;
        this.stockReceiptMapper = stockReceiptMapper;
    }

    /**
     * Return a {@link Page} of {@link StockReceiptDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockReceiptDTO> findByCriteria(StockReceiptCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockReceipt> specification = createSpecification(criteria);
        return stockReceiptRepository.findAll(specification, page).map(stockReceiptMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockReceiptCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StockReceipt> specification = createSpecification(criteria);
        return stockReceiptRepository.count(specification);
    }

    /**
     * Function to convert {@link StockReceiptCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockReceipt> createSpecification(StockReceiptCriteria criteria) {
        Specification<StockReceipt> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockReceipt_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), StockReceipt_.code));
            }
            if (criteria.getReceiptDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getReceiptDate(), StockReceipt_.receiptDate));
            }
            if (criteria.getSource() != null) {
                specification = specification.and(buildSpecification(criteria.getSource(), StockReceipt_.source));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), StockReceipt_.status));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), StockReceipt_.note));
            }
        }
        return specification;
    }
}

package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.StockIssue;
import com.gindevp.app.repository.StockIssueRepository;
import com.gindevp.app.service.criteria.StockIssueCriteria;
import com.gindevp.app.service.dto.StockIssueDTO;
import com.gindevp.app.service.mapper.StockIssueMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link StockIssue} entities in the database.
 * The main input is a {@link StockIssueCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link StockIssueDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class StockIssueQueryService extends QueryService<StockIssue> {

    private static final Logger LOG = LoggerFactory.getLogger(StockIssueQueryService.class);

    private final StockIssueRepository stockIssueRepository;

    private final StockIssueMapper stockIssueMapper;

    public StockIssueQueryService(StockIssueRepository stockIssueRepository, StockIssueMapper stockIssueMapper) {
        this.stockIssueRepository = stockIssueRepository;
        this.stockIssueMapper = stockIssueMapper;
    }

    /**
     * Return a {@link Page} of {@link StockIssueDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<StockIssueDTO> findByCriteria(StockIssueCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<StockIssue> specification = createSpecification(criteria);
        return stockIssueRepository.findAll(specification, page).map(stockIssueMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(StockIssueCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<StockIssue> specification = createSpecification(criteria);
        return stockIssueRepository.count(specification);
    }

    /**
     * Function to convert {@link StockIssueCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<StockIssue> createSpecification(StockIssueCriteria criteria) {
        Specification<StockIssue> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), StockIssue_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), StockIssue_.code));
            }
            if (criteria.getIssueDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIssueDate(), StockIssue_.issueDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), StockIssue_.status));
            }
            if (criteria.getAssigneeType() != null) {
                specification = specification.and(buildSpecification(criteria.getAssigneeType(), StockIssue_.assigneeType));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), StockIssue_.note));
            }
            if (criteria.getEmployeeId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getEmployeeId(), root -> root.join(StockIssue_.employee, JoinType.LEFT).get(Employee_.id))
                );
            }
            if (criteria.getDepartmentId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getDepartmentId(), root ->
                        root.join(StockIssue_.department, JoinType.LEFT).get(Department_.id)
                    )
                );
            }
            if (criteria.getLocationId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getLocationId(), root -> root.join(StockIssue_.location, JoinType.LEFT).get(Location_.id))
                );
            }
        }
        return specification;
    }
}

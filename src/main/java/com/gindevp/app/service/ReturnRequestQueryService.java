package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.ReturnRequest;
import com.gindevp.app.repository.ReturnRequestRepository;
import com.gindevp.app.service.criteria.ReturnRequestCriteria;
import com.gindevp.app.service.dto.ReturnRequestDTO;
import com.gindevp.app.service.mapper.ReturnRequestMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.LongFilter;

/**
 * Service for executing complex queries for {@link ReturnRequest} entities in the database.
 * The main input is a {@link ReturnRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link ReturnRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ReturnRequestQueryService extends QueryService<ReturnRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnRequestQueryService.class);

    private final ReturnRequestRepository returnRequestRepository;

    private final ReturnRequestMapper returnRequestMapper;

    private final CurrentEmployeeService currentEmployeeService;

    public ReturnRequestQueryService(
        ReturnRequestRepository returnRequestRepository,
        ReturnRequestMapper returnRequestMapper,
        CurrentEmployeeService currentEmployeeService
    ) {
        this.returnRequestRepository = returnRequestRepository;
        this.returnRequestMapper = returnRequestMapper;
        this.currentEmployeeService = currentEmployeeService;
    }

    /**
     * Return a {@link Page} of {@link ReturnRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ReturnRequestDTO> findByCriteria(ReturnRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        ReturnRequestCriteria c = criteria != null ? criteria : new ReturnRequestCriteria();
        applyRequesterScope(c);
        final Specification<ReturnRequest> specification = createSpecification(c);
        return returnRequestRepository.findAll(specification, page).map(returnRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ReturnRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        ReturnRequestCriteria c = criteria != null ? criteria : new ReturnRequestCriteria();
        applyRequesterScope(c);
        final Specification<ReturnRequest> specification = createSpecification(c);
        return returnRequestRepository.count(specification);
    }

    private void applyRequesterScope(ReturnRequestCriteria c) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return;
        }
        LongFilter lf = new LongFilter();
        lf.setEquals(currentEmployeeService.currentEmployeeId().orElse(-1L));
        c.setRequesterId(lf);
    }

    /**
     * Function to convert {@link ReturnRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<ReturnRequest> createSpecification(ReturnRequestCriteria criteria) {
        Specification<ReturnRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), ReturnRequest_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), ReturnRequest_.code));
            }
            if (criteria.getRequestDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRequestDate(), ReturnRequest_.requestDate));
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), ReturnRequest_.note));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), ReturnRequest_.status));
            }
            if (criteria.getRequesterId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRequesterId(), root ->
                        root.join(ReturnRequest_.requester, JoinType.LEFT).get(Employee_.id)
                    )
                );
            }
        }
        return specification;
    }
}

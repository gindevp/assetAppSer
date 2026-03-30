package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.AllocationRequest;
import com.gindevp.app.repository.AllocationRequestRepository;
import com.gindevp.app.service.criteria.AllocationRequestCriteria;
import com.gindevp.app.service.dto.AllocationRequestDTO;
import com.gindevp.app.service.mapper.AllocationRequestMapper;
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
 * Service for executing complex queries for {@link AllocationRequest} entities in the database.
 * The main input is a {@link AllocationRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AllocationRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AllocationRequestQueryService extends QueryService<AllocationRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(AllocationRequestQueryService.class);

    private final AllocationRequestRepository allocationRequestRepository;

    private final AllocationRequestMapper allocationRequestMapper;

    private final CurrentEmployeeService currentEmployeeService;

    public AllocationRequestQueryService(
        AllocationRequestRepository allocationRequestRepository,
        AllocationRequestMapper allocationRequestMapper,
        CurrentEmployeeService currentEmployeeService
    ) {
        this.allocationRequestRepository = allocationRequestRepository;
        this.allocationRequestMapper = allocationRequestMapper;
        this.currentEmployeeService = currentEmployeeService;
    }

    /**
     * Return a {@link Page} of {@link AllocationRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AllocationRequestDTO> findByCriteria(AllocationRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AllocationRequest> specification = createSpecification(criteria);
        return allocationRequestRepository.findAll(specification, page).map(allocationRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AllocationRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        AllocationRequestCriteria c = criteria != null ? criteria : new AllocationRequestCriteria();
        applyRequesterScope(c);
        final Specification<AllocationRequest> specification = createSpecification(c);
        return allocationRequestRepository.count(specification);
    }

    private void applyRequesterScope(AllocationRequestCriteria c) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return;
        }
        LongFilter lf = new LongFilter();
        lf.setEquals(currentEmployeeService.currentEmployeeId().orElse(-1L));
        c.setRequesterId(lf);
    }

    /**
     * Function to convert {@link AllocationRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AllocationRequest> createSpecification(AllocationRequestCriteria criteria) {
        Specification<AllocationRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AllocationRequest_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), AllocationRequest_.code));
            }
            if (criteria.getRequestDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRequestDate(), AllocationRequest_.requestDate));
            }
            if (criteria.getReason() != null) {
                specification = specification.and(buildStringSpecification(criteria.getReason(), AllocationRequest_.reason));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), AllocationRequest_.status));
            }
            if (criteria.getBeneficiaryNote() != null) {
                specification = specification.and(
                    buildStringSpecification(criteria.getBeneficiaryNote(), AllocationRequest_.beneficiaryNote)
                );
            }
            if (criteria.getRequesterId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRequesterId(), root ->
                        root.join(AllocationRequest_.requester, JoinType.LEFT).get(Employee_.id)
                    )
                );
            }
        }
        return specification;
    }
}

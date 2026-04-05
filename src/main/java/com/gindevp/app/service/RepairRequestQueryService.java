package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.domain.RepairRequestLine;
import com.gindevp.app.repository.RepairRequestRepository;
import com.gindevp.app.service.criteria.RepairRequestCriteria;
import com.gindevp.app.service.dto.RepairRequestDTO;
import com.gindevp.app.service.mapper.RepairRequestMapper;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
 * Service for executing complex queries for {@link RepairRequest} entities in the database.
 * The main input is a {@link RepairRequestCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RepairRequestDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RepairRequestQueryService extends QueryService<RepairRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RepairRequestQueryService.class);

    private final RepairRequestRepository repairRequestRepository;

    private final RepairRequestMapper repairRequestMapper;

    private final CurrentEmployeeService currentEmployeeService;

    public RepairRequestQueryService(
        RepairRequestRepository repairRequestRepository,
        RepairRequestMapper repairRequestMapper,
        CurrentEmployeeService currentEmployeeService
    ) {
        this.repairRequestRepository = repairRequestRepository;
        this.repairRequestMapper = repairRequestMapper;
        this.currentEmployeeService = currentEmployeeService;
    }

    /**
     * Return a {@link Page} of {@link RepairRequestDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RepairRequestDTO> findByCriteria(RepairRequestCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RepairRequest> specification = createSpecification(criteria);
        return repairRequestRepository.findAll(specification, page).map(repairRequestMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RepairRequestCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        RepairRequestCriteria c = criteria != null ? criteria : new RepairRequestCriteria();
        applyRequesterScope(c);
        final Specification<RepairRequest> specification = createSpecification(c);
        return repairRequestRepository.count(specification);
    }

    private void applyRequesterScope(RepairRequestCriteria c) {
        if (currentEmployeeService.isAssetManagerOrAdmin()) {
            return;
        }
        LongFilter lf = new LongFilter();
        lf.setEquals(currentEmployeeService.currentEmployeeId().orElse(-1L));
        c.setRequesterId(lf);
    }

    /**
     * Function to convert {@link RepairRequestCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RepairRequest> createSpecification(RepairRequestCriteria criteria) {
        Specification<RepairRequest> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RepairRequest_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), RepairRequest_.code));
            }
            if (criteria.getRequestDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getRequestDate(), RepairRequest_.requestDate));
            }
            if (criteria.getProblemCategory() != null) {
                specification = specification.and(buildStringSpecification(criteria.getProblemCategory(), RepairRequest_.problemCategory));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), RepairRequest_.description));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), RepairRequest_.status));
            }
            if (criteria.getResolutionNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getResolutionNote(), RepairRequest_.resolutionNote));
            }
            if (criteria.getRequesterId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getRequesterId(), root ->
                        root.join(RepairRequest_.requester, JoinType.LEFT).get(Employee_.id)
                    )
                );
            }
            if (criteria.getEquipmentId() != null) {
                specification = specification.and((root, query, cb) -> {
                    Long eqId = criteria.getEquipmentId().getEquals();
                    if (eqId == null) {
                        return cb.conjunction();
                    }
                    Subquery<Integer> sq = query.subquery(Integer.class);
                    Root<RepairRequestLine> lineRoot = sq.from(RepairRequestLine.class);
                    sq.select(cb.literal(1))
                        .where(
                            cb.and(
                                cb.equal(lineRoot.get("repairRequest"), root),
                                cb.equal(lineRoot.get("equipment").get("id"), eqId)
                            )
                        );
                    return cb.or(cb.equal(root.get("equipment").get("id"), eqId), cb.exists(sq));
                });
            }
        }
        return specification;
    }
}

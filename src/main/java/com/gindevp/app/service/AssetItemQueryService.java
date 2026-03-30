package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.repository.AssetItemRepository;
import com.gindevp.app.service.criteria.AssetItemCriteria;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.mapper.AssetItemMapper;
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
 * Service for executing complex queries for {@link AssetItem} entities in the database.
 * The main input is a {@link AssetItemCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AssetItemDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AssetItemQueryService extends QueryService<AssetItem> {

    private static final Logger LOG = LoggerFactory.getLogger(AssetItemQueryService.class);

    private final AssetItemRepository assetItemRepository;

    private final AssetItemMapper assetItemMapper;

    public AssetItemQueryService(AssetItemRepository assetItemRepository, AssetItemMapper assetItemMapper) {
        this.assetItemRepository = assetItemRepository;
        this.assetItemMapper = assetItemMapper;
    }

    /**
     * Return a {@link Page} of {@link AssetItemDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AssetItemDTO> findByCriteria(AssetItemCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AssetItem> specification = createSpecification(criteria);
        return assetItemRepository.findAll(specification, page).map(assetItemMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AssetItemCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AssetItem> specification = createSpecification(criteria);
        return assetItemRepository.count(specification);
    }

    /**
     * Function to convert {@link AssetItemCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AssetItem> createSpecification(AssetItemCriteria criteria) {
        Specification<AssetItem> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), AssetItem_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), AssetItem_.code));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), AssetItem_.name));
            }
            if (criteria.getManagementType() != null) {
                specification = specification.and(buildSpecification(criteria.getManagementType(), AssetItem_.managementType));
            }
            if (criteria.getUnit() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUnit(), AssetItem_.unit));
            }
            if (criteria.getDepreciationEnabled() != null) {
                specification = specification.and(buildSpecification(criteria.getDepreciationEnabled(), AssetItem_.depreciationEnabled));
            }
            if (criteria.getSerialTrackingRequired() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSerialTrackingRequired(), AssetItem_.serialTrackingRequired)
                );
            }
            if (criteria.getNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getNote(), AssetItem_.note));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildSpecification(criteria.getActive(), AssetItem_.active));
            }
            if (criteria.getAssetLineId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssetLineId(), root -> root.join(AssetItem_.assetLine, JoinType.LEFT).get(AssetLine_.id))
                );
            }
        }
        return specification;
    }
}

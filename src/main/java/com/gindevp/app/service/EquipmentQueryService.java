package com.gindevp.app.service;

import com.gindevp.app.domain.*; // for static metamodels
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.repository.EquipmentRepository;
import com.gindevp.app.service.criteria.EquipmentCriteria;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.mapper.EquipmentMapper;
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
 * Service for executing complex queries for {@link Equipment} entities in the database.
 * The main input is a {@link EquipmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EquipmentDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EquipmentQueryService extends QueryService<Equipment> {

    private static final Logger LOG = LoggerFactory.getLogger(EquipmentQueryService.class);

    private final EquipmentRepository equipmentRepository;

    private final EquipmentMapper equipmentMapper;

    public EquipmentQueryService(EquipmentRepository equipmentRepository, EquipmentMapper equipmentMapper) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentMapper = equipmentMapper;
    }

    /**
     * Return a {@link Page} of {@link EquipmentDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EquipmentDTO> findByCriteria(EquipmentCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Equipment> specification = createSpecification(criteria);
        return equipmentRepository.findAll(specification, page).map(equipmentMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EquipmentCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Equipment> specification = createSpecification(criteria);
        return equipmentRepository.count(specification);
    }

    /**
     * Function to convert {@link EquipmentCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Equipment> createSpecification(EquipmentCriteria criteria) {
        Specification<Equipment> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Equipment_.id));
            }
            if (criteria.getEquipmentCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getEquipmentCode(), Equipment_.equipmentCode));
            }
            if (criteria.getSerial() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSerial(), Equipment_.serial));
            }
            if (criteria.getConditionNote() != null) {
                specification = specification.and(buildStringSpecification(criteria.getConditionNote(), Equipment_.conditionNote));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Equipment_.status));
            }
            if (criteria.getPurchasePrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPurchasePrice(), Equipment_.purchasePrice));
            }
            if (criteria.getCapitalizationDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCapitalizationDate(), Equipment_.capitalizationDate));
            }
            if (criteria.getDepreciationMonths() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDepreciationMonths(), Equipment_.depreciationMonths));
            }
            if (criteria.getSalvageValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSalvageValue(), Equipment_.salvageValue));
            }
            if (criteria.getBookValueSnapshot() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBookValueSnapshot(), Equipment_.bookValueSnapshot));
            }
            if (criteria.getAssetItemId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAssetItemId(), root -> root.join(Equipment_.assetItem, JoinType.LEFT).get(AssetItem_.id))
                );
            }
            if (criteria.getSupplierId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getSupplierId(), root -> root.join(Equipment_.supplier, JoinType.LEFT).get(Supplier_.id))
                );
            }
        }
        return specification;
    }
}

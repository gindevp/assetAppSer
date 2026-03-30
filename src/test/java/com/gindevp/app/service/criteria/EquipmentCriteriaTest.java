package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EquipmentCriteriaTest {

    @Test
    void newEquipmentCriteriaHasAllFiltersNullTest() {
        var equipmentCriteria = new EquipmentCriteria();
        assertThat(equipmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void equipmentCriteriaFluentMethodsCreatesFiltersTest() {
        var equipmentCriteria = new EquipmentCriteria();

        setAllFilters(equipmentCriteria);

        assertThat(equipmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void equipmentCriteriaCopyCreatesNullFilterTest() {
        var equipmentCriteria = new EquipmentCriteria();
        var copy = equipmentCriteria.copy();

        assertThat(equipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(equipmentCriteria)
        );
    }

    @Test
    void equipmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var equipmentCriteria = new EquipmentCriteria();
        setAllFilters(equipmentCriteria);

        var copy = equipmentCriteria.copy();

        assertThat(equipmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(equipmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var equipmentCriteria = new EquipmentCriteria();

        assertThat(equipmentCriteria).hasToString("EquipmentCriteria{}");
    }

    private static void setAllFilters(EquipmentCriteria equipmentCriteria) {
        equipmentCriteria.id();
        equipmentCriteria.equipmentCode();
        equipmentCriteria.serial();
        equipmentCriteria.conditionNote();
        equipmentCriteria.status();
        equipmentCriteria.purchasePrice();
        equipmentCriteria.capitalizationDate();
        equipmentCriteria.depreciationMonths();
        equipmentCriteria.salvageValue();
        equipmentCriteria.bookValueSnapshot();
        equipmentCriteria.assetItemId();
        equipmentCriteria.supplierId();
        equipmentCriteria.distinct();
    }

    private static Condition<EquipmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getEquipmentCode()) &&
                condition.apply(criteria.getSerial()) &&
                condition.apply(criteria.getConditionNote()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getPurchasePrice()) &&
                condition.apply(criteria.getCapitalizationDate()) &&
                condition.apply(criteria.getDepreciationMonths()) &&
                condition.apply(criteria.getSalvageValue()) &&
                condition.apply(criteria.getBookValueSnapshot()) &&
                condition.apply(criteria.getAssetItemId()) &&
                condition.apply(criteria.getSupplierId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EquipmentCriteria> copyFiltersAre(EquipmentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getEquipmentCode(), copy.getEquipmentCode()) &&
                condition.apply(criteria.getSerial(), copy.getSerial()) &&
                condition.apply(criteria.getConditionNote(), copy.getConditionNote()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getPurchasePrice(), copy.getPurchasePrice()) &&
                condition.apply(criteria.getCapitalizationDate(), copy.getCapitalizationDate()) &&
                condition.apply(criteria.getDepreciationMonths(), copy.getDepreciationMonths()) &&
                condition.apply(criteria.getSalvageValue(), copy.getSalvageValue()) &&
                condition.apply(criteria.getBookValueSnapshot(), copy.getBookValueSnapshot()) &&
                condition.apply(criteria.getAssetItemId(), copy.getAssetItemId()) &&
                condition.apply(criteria.getSupplierId(), copy.getSupplierId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

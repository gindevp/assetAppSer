package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class RepairRequestCriteriaTest {

    @Test
    void newRepairRequestCriteriaHasAllFiltersNullTest() {
        var repairRequestCriteria = new RepairRequestCriteria();
        assertThat(repairRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void repairRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var repairRequestCriteria = new RepairRequestCriteria();

        setAllFilters(repairRequestCriteria);

        assertThat(repairRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void repairRequestCriteriaCopyCreatesNullFilterTest() {
        var repairRequestCriteria = new RepairRequestCriteria();
        var copy = repairRequestCriteria.copy();

        assertThat(repairRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(repairRequestCriteria)
        );
    }

    @Test
    void repairRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var repairRequestCriteria = new RepairRequestCriteria();
        setAllFilters(repairRequestCriteria);

        var copy = repairRequestCriteria.copy();

        assertThat(repairRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(repairRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var repairRequestCriteria = new RepairRequestCriteria();

        assertThat(repairRequestCriteria).hasToString("RepairRequestCriteria{}");
    }

    private static void setAllFilters(RepairRequestCriteria repairRequestCriteria) {
        repairRequestCriteria.id();
        repairRequestCriteria.code();
        repairRequestCriteria.requestDate();
        repairRequestCriteria.problemCategory();
        repairRequestCriteria.description();
        repairRequestCriteria.status();
        repairRequestCriteria.resolutionNote();
        repairRequestCriteria.requesterId();
        repairRequestCriteria.equipmentId();
        repairRequestCriteria.distinct();
    }

    private static Condition<RepairRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getRequestDate()) &&
                condition.apply(criteria.getProblemCategory()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getResolutionNote()) &&
                condition.apply(criteria.getRequesterId()) &&
                condition.apply(criteria.getEquipmentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<RepairRequestCriteria> copyFiltersAre(
        RepairRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getRequestDate(), copy.getRequestDate()) &&
                condition.apply(criteria.getProblemCategory(), copy.getProblemCategory()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getResolutionNote(), copy.getResolutionNote()) &&
                condition.apply(criteria.getRequesterId(), copy.getRequesterId()) &&
                condition.apply(criteria.getEquipmentId(), copy.getEquipmentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

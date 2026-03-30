package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AllocationRequestCriteriaTest {

    @Test
    void newAllocationRequestCriteriaHasAllFiltersNullTest() {
        var allocationRequestCriteria = new AllocationRequestCriteria();
        assertThat(allocationRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void allocationRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var allocationRequestCriteria = new AllocationRequestCriteria();

        setAllFilters(allocationRequestCriteria);

        assertThat(allocationRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void allocationRequestCriteriaCopyCreatesNullFilterTest() {
        var allocationRequestCriteria = new AllocationRequestCriteria();
        var copy = allocationRequestCriteria.copy();

        assertThat(allocationRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(allocationRequestCriteria)
        );
    }

    @Test
    void allocationRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var allocationRequestCriteria = new AllocationRequestCriteria();
        setAllFilters(allocationRequestCriteria);

        var copy = allocationRequestCriteria.copy();

        assertThat(allocationRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(allocationRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var allocationRequestCriteria = new AllocationRequestCriteria();

        assertThat(allocationRequestCriteria).hasToString("AllocationRequestCriteria{}");
    }

    private static void setAllFilters(AllocationRequestCriteria allocationRequestCriteria) {
        allocationRequestCriteria.id();
        allocationRequestCriteria.code();
        allocationRequestCriteria.requestDate();
        allocationRequestCriteria.reason();
        allocationRequestCriteria.status();
        allocationRequestCriteria.beneficiaryNote();
        allocationRequestCriteria.requesterId();
        allocationRequestCriteria.distinct();
    }

    private static Condition<AllocationRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getRequestDate()) &&
                condition.apply(criteria.getReason()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getBeneficiaryNote()) &&
                condition.apply(criteria.getRequesterId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AllocationRequestCriteria> copyFiltersAre(
        AllocationRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getRequestDate(), copy.getRequestDate()) &&
                condition.apply(criteria.getReason(), copy.getReason()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getBeneficiaryNote(), copy.getBeneficiaryNote()) &&
                condition.apply(criteria.getRequesterId(), copy.getRequesterId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

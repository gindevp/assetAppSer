package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ReturnRequestCriteriaTest {

    @Test
    void newReturnRequestCriteriaHasAllFiltersNullTest() {
        var returnRequestCriteria = new ReturnRequestCriteria();
        assertThat(returnRequestCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void returnRequestCriteriaFluentMethodsCreatesFiltersTest() {
        var returnRequestCriteria = new ReturnRequestCriteria();

        setAllFilters(returnRequestCriteria);

        assertThat(returnRequestCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void returnRequestCriteriaCopyCreatesNullFilterTest() {
        var returnRequestCriteria = new ReturnRequestCriteria();
        var copy = returnRequestCriteria.copy();

        assertThat(returnRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(returnRequestCriteria)
        );
    }

    @Test
    void returnRequestCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var returnRequestCriteria = new ReturnRequestCriteria();
        setAllFilters(returnRequestCriteria);

        var copy = returnRequestCriteria.copy();

        assertThat(returnRequestCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(returnRequestCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var returnRequestCriteria = new ReturnRequestCriteria();

        assertThat(returnRequestCriteria).hasToString("ReturnRequestCriteria{}");
    }

    private static void setAllFilters(ReturnRequestCriteria returnRequestCriteria) {
        returnRequestCriteria.id();
        returnRequestCriteria.code();
        returnRequestCriteria.requestDate();
        returnRequestCriteria.note();
        returnRequestCriteria.status();
        returnRequestCriteria.requesterId();
        returnRequestCriteria.distinct();
    }

    private static Condition<ReturnRequestCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getRequestDate()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getRequesterId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ReturnRequestCriteria> copyFiltersAre(
        ReturnRequestCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getRequestDate(), copy.getRequestDate()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getRequesterId(), copy.getRequesterId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

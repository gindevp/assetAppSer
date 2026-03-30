package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StockIssueCriteriaTest {

    @Test
    void newStockIssueCriteriaHasAllFiltersNullTest() {
        var stockIssueCriteria = new StockIssueCriteria();
        assertThat(stockIssueCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stockIssueCriteriaFluentMethodsCreatesFiltersTest() {
        var stockIssueCriteria = new StockIssueCriteria();

        setAllFilters(stockIssueCriteria);

        assertThat(stockIssueCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stockIssueCriteriaCopyCreatesNullFilterTest() {
        var stockIssueCriteria = new StockIssueCriteria();
        var copy = stockIssueCriteria.copy();

        assertThat(stockIssueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stockIssueCriteria)
        );
    }

    @Test
    void stockIssueCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stockIssueCriteria = new StockIssueCriteria();
        setAllFilters(stockIssueCriteria);

        var copy = stockIssueCriteria.copy();

        assertThat(stockIssueCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stockIssueCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stockIssueCriteria = new StockIssueCriteria();

        assertThat(stockIssueCriteria).hasToString("StockIssueCriteria{}");
    }

    private static void setAllFilters(StockIssueCriteria stockIssueCriteria) {
        stockIssueCriteria.id();
        stockIssueCriteria.code();
        stockIssueCriteria.issueDate();
        stockIssueCriteria.status();
        stockIssueCriteria.assigneeType();
        stockIssueCriteria.note();
        stockIssueCriteria.issueId();
        stockIssueCriteria.employeeId();
        stockIssueCriteria.departmentId();
        stockIssueCriteria.locationId();
        stockIssueCriteria.distinct();
    }

    private static Condition<StockIssueCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getIssueDate()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getAssigneeType()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getIssueId()) &&
                condition.apply(criteria.getEmployeeId()) &&
                condition.apply(criteria.getDepartmentId()) &&
                condition.apply(criteria.getLocationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StockIssueCriteria> copyFiltersAre(StockIssueCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getIssueDate(), copy.getIssueDate()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getAssigneeType(), copy.getAssigneeType()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getIssueId(), copy.getIssueId()) &&
                condition.apply(criteria.getEmployeeId(), copy.getEmployeeId()) &&
                condition.apply(criteria.getDepartmentId(), copy.getDepartmentId()) &&
                condition.apply(criteria.getLocationId(), copy.getLocationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

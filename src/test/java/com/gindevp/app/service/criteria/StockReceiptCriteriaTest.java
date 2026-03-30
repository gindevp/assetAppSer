package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class StockReceiptCriteriaTest {

    @Test
    void newStockReceiptCriteriaHasAllFiltersNullTest() {
        var stockReceiptCriteria = new StockReceiptCriteria();
        assertThat(stockReceiptCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void stockReceiptCriteriaFluentMethodsCreatesFiltersTest() {
        var stockReceiptCriteria = new StockReceiptCriteria();

        setAllFilters(stockReceiptCriteria);

        assertThat(stockReceiptCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void stockReceiptCriteriaCopyCreatesNullFilterTest() {
        var stockReceiptCriteria = new StockReceiptCriteria();
        var copy = stockReceiptCriteria.copy();

        assertThat(stockReceiptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(stockReceiptCriteria)
        );
    }

    @Test
    void stockReceiptCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var stockReceiptCriteria = new StockReceiptCriteria();
        setAllFilters(stockReceiptCriteria);

        var copy = stockReceiptCriteria.copy();

        assertThat(stockReceiptCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(stockReceiptCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var stockReceiptCriteria = new StockReceiptCriteria();

        assertThat(stockReceiptCriteria).hasToString("StockReceiptCriteria{}");
    }

    private static void setAllFilters(StockReceiptCriteria stockReceiptCriteria) {
        stockReceiptCriteria.id();
        stockReceiptCriteria.code();
        stockReceiptCriteria.receiptDate();
        stockReceiptCriteria.source();
        stockReceiptCriteria.status();
        stockReceiptCriteria.note();
        stockReceiptCriteria.distinct();
    }

    private static Condition<StockReceiptCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getReceiptDate()) &&
                condition.apply(criteria.getSource()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<StockReceiptCriteria> copyFiltersAre(
        StockReceiptCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getReceiptDate(), copy.getReceiptDate()) &&
                condition.apply(criteria.getSource(), copy.getSource()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

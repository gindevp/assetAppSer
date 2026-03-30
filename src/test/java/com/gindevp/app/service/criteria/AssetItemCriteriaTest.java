package com.gindevp.app.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AssetItemCriteriaTest {

    @Test
    void newAssetItemCriteriaHasAllFiltersNullTest() {
        var assetItemCriteria = new AssetItemCriteria();
        assertThat(assetItemCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void assetItemCriteriaFluentMethodsCreatesFiltersTest() {
        var assetItemCriteria = new AssetItemCriteria();

        setAllFilters(assetItemCriteria);

        assertThat(assetItemCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void assetItemCriteriaCopyCreatesNullFilterTest() {
        var assetItemCriteria = new AssetItemCriteria();
        var copy = assetItemCriteria.copy();

        assertThat(assetItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(assetItemCriteria)
        );
    }

    @Test
    void assetItemCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var assetItemCriteria = new AssetItemCriteria();
        setAllFilters(assetItemCriteria);

        var copy = assetItemCriteria.copy();

        assertThat(assetItemCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(assetItemCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var assetItemCriteria = new AssetItemCriteria();

        assertThat(assetItemCriteria).hasToString("AssetItemCriteria{}");
    }

    private static void setAllFilters(AssetItemCriteria assetItemCriteria) {
        assetItemCriteria.id();
        assetItemCriteria.code();
        assetItemCriteria.name();
        assetItemCriteria.managementType();
        assetItemCriteria.unit();
        assetItemCriteria.depreciationEnabled();
        assetItemCriteria.serialTrackingRequired();
        assetItemCriteria.note();
        assetItemCriteria.active();
        assetItemCriteria.assetLineId();
        assetItemCriteria.distinct();
    }

    private static Condition<AssetItemCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getManagementType()) &&
                condition.apply(criteria.getUnit()) &&
                condition.apply(criteria.getDepreciationEnabled()) &&
                condition.apply(criteria.getSerialTrackingRequired()) &&
                condition.apply(criteria.getNote()) &&
                condition.apply(criteria.getActive()) &&
                condition.apply(criteria.getAssetLineId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AssetItemCriteria> copyFiltersAre(AssetItemCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getManagementType(), copy.getManagementType()) &&
                condition.apply(criteria.getUnit(), copy.getUnit()) &&
                condition.apply(criteria.getDepreciationEnabled(), copy.getDepreciationEnabled()) &&
                condition.apply(criteria.getSerialTrackingRequired(), copy.getSerialTrackingRequired()) &&
                condition.apply(criteria.getNote(), copy.getNote()) &&
                condition.apply(criteria.getActive(), copy.getActive()) &&
                condition.apply(criteria.getAssetLineId(), copy.getAssetLineId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

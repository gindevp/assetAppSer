package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.AssetManagementType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.AssetItem} entity. This class is used
 * in {@link com.gindevp.app.web.rest.AssetItemResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /asset-items?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssetItemCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AssetManagementType
     */
    public static class AssetManagementTypeFilter extends Filter<AssetManagementType> {

        public AssetManagementTypeFilter() {}

        public AssetManagementTypeFilter(AssetManagementTypeFilter filter) {
            super(filter);
        }

        @Override
        public AssetManagementTypeFilter copy() {
            return new AssetManagementTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private StringFilter name;

    private AssetManagementTypeFilter managementType;

    private StringFilter unit;

    private BooleanFilter depreciationEnabled;

    private BooleanFilter serialTrackingRequired;

    private StringFilter note;

    private BooleanFilter active;

    private LongFilter assetLineId;

    private Boolean distinct;

    public AssetItemCriteria() {}

    public AssetItemCriteria(AssetItemCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.managementType = other.optionalManagementType().map(AssetManagementTypeFilter::copy).orElse(null);
        this.unit = other.optionalUnit().map(StringFilter::copy).orElse(null);
        this.depreciationEnabled = other.optionalDepreciationEnabled().map(BooleanFilter::copy).orElse(null);
        this.serialTrackingRequired = other.optionalSerialTrackingRequired().map(BooleanFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.active = other.optionalActive().map(BooleanFilter::copy).orElse(null);
        this.assetLineId = other.optionalAssetLineId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AssetItemCriteria copy() {
        return new AssetItemCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public AssetManagementTypeFilter getManagementType() {
        return managementType;
    }

    public Optional<AssetManagementTypeFilter> optionalManagementType() {
        return Optional.ofNullable(managementType);
    }

    public AssetManagementTypeFilter managementType() {
        if (managementType == null) {
            setManagementType(new AssetManagementTypeFilter());
        }
        return managementType;
    }

    public void setManagementType(AssetManagementTypeFilter managementType) {
        this.managementType = managementType;
    }

    public StringFilter getUnit() {
        return unit;
    }

    public Optional<StringFilter> optionalUnit() {
        return Optional.ofNullable(unit);
    }

    public StringFilter unit() {
        if (unit == null) {
            setUnit(new StringFilter());
        }
        return unit;
    }

    public void setUnit(StringFilter unit) {
        this.unit = unit;
    }

    public BooleanFilter getDepreciationEnabled() {
        return depreciationEnabled;
    }

    public Optional<BooleanFilter> optionalDepreciationEnabled() {
        return Optional.ofNullable(depreciationEnabled);
    }

    public BooleanFilter depreciationEnabled() {
        if (depreciationEnabled == null) {
            setDepreciationEnabled(new BooleanFilter());
        }
        return depreciationEnabled;
    }

    public void setDepreciationEnabled(BooleanFilter depreciationEnabled) {
        this.depreciationEnabled = depreciationEnabled;
    }

    public BooleanFilter getSerialTrackingRequired() {
        return serialTrackingRequired;
    }

    public Optional<BooleanFilter> optionalSerialTrackingRequired() {
        return Optional.ofNullable(serialTrackingRequired);
    }

    public BooleanFilter serialTrackingRequired() {
        if (serialTrackingRequired == null) {
            setSerialTrackingRequired(new BooleanFilter());
        }
        return serialTrackingRequired;
    }

    public void setSerialTrackingRequired(BooleanFilter serialTrackingRequired) {
        this.serialTrackingRequired = serialTrackingRequired;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public BooleanFilter getActive() {
        return active;
    }

    public Optional<BooleanFilter> optionalActive() {
        return Optional.ofNullable(active);
    }

    public BooleanFilter active() {
        if (active == null) {
            setActive(new BooleanFilter());
        }
        return active;
    }

    public void setActive(BooleanFilter active) {
        this.active = active;
    }

    public LongFilter getAssetLineId() {
        return assetLineId;
    }

    public Optional<LongFilter> optionalAssetLineId() {
        return Optional.ofNullable(assetLineId);
    }

    public LongFilter assetLineId() {
        if (assetLineId == null) {
            setAssetLineId(new LongFilter());
        }
        return assetLineId;
    }

    public void setAssetLineId(LongFilter assetLineId) {
        this.assetLineId = assetLineId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AssetItemCriteria that = (AssetItemCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(name, that.name) &&
            Objects.equals(managementType, that.managementType) &&
            Objects.equals(unit, that.unit) &&
            Objects.equals(depreciationEnabled, that.depreciationEnabled) &&
            Objects.equals(serialTrackingRequired, that.serialTrackingRequired) &&
            Objects.equals(note, that.note) &&
            Objects.equals(active, that.active) &&
            Objects.equals(assetLineId, that.assetLineId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            name,
            managementType,
            unit,
            depreciationEnabled,
            serialTrackingRequired,
            note,
            active,
            assetLineId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssetItemCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalManagementType().map(f -> "managementType=" + f + ", ").orElse("") +
            optionalUnit().map(f -> "unit=" + f + ", ").orElse("") +
            optionalDepreciationEnabled().map(f -> "depreciationEnabled=" + f + ", ").orElse("") +
            optionalSerialTrackingRequired().map(f -> "serialTrackingRequired=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalActive().map(f -> "active=" + f + ", ").orElse("") +
            optionalAssetLineId().map(f -> "assetLineId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

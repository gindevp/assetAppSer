package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.Equipment} entity. This class is used
 * in {@link com.gindevp.app.web.rest.EquipmentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /equipment?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentCriteria implements Serializable, Criteria {

    /**
     * Class for filtering EquipmentOperationalStatus
     */
    public static class EquipmentOperationalStatusFilter extends Filter<EquipmentOperationalStatus> {

        public EquipmentOperationalStatusFilter() {}

        public EquipmentOperationalStatusFilter(EquipmentOperationalStatusFilter filter) {
            super(filter);
        }

        @Override
        public EquipmentOperationalStatusFilter copy() {
            return new EquipmentOperationalStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter equipmentCode;

    private StringFilter serial;

    private StringFilter conditionNote;

    private EquipmentOperationalStatusFilter status;

    private BigDecimalFilter purchasePrice;

    private LocalDateFilter capitalizationDate;

    private IntegerFilter depreciationMonths;

    private BigDecimalFilter salvageValue;

    private BigDecimalFilter bookValueSnapshot;

    private LongFilter assetItemId;

    private LongFilter supplierId;

    private Boolean distinct;

    public EquipmentCriteria() {}

    public EquipmentCriteria(EquipmentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.equipmentCode = other.optionalEquipmentCode().map(StringFilter::copy).orElse(null);
        this.serial = other.optionalSerial().map(StringFilter::copy).orElse(null);
        this.conditionNote = other.optionalConditionNote().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(EquipmentOperationalStatusFilter::copy).orElse(null);
        this.purchasePrice = other.optionalPurchasePrice().map(BigDecimalFilter::copy).orElse(null);
        this.capitalizationDate = other.optionalCapitalizationDate().map(LocalDateFilter::copy).orElse(null);
        this.depreciationMonths = other.optionalDepreciationMonths().map(IntegerFilter::copy).orElse(null);
        this.salvageValue = other.optionalSalvageValue().map(BigDecimalFilter::copy).orElse(null);
        this.bookValueSnapshot = other.optionalBookValueSnapshot().map(BigDecimalFilter::copy).orElse(null);
        this.assetItemId = other.optionalAssetItemId().map(LongFilter::copy).orElse(null);
        this.supplierId = other.optionalSupplierId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EquipmentCriteria copy() {
        return new EquipmentCriteria(this);
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

    public StringFilter getEquipmentCode() {
        return equipmentCode;
    }

    public Optional<StringFilter> optionalEquipmentCode() {
        return Optional.ofNullable(equipmentCode);
    }

    public StringFilter equipmentCode() {
        if (equipmentCode == null) {
            setEquipmentCode(new StringFilter());
        }
        return equipmentCode;
    }

    public void setEquipmentCode(StringFilter equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public StringFilter getSerial() {
        return serial;
    }

    public Optional<StringFilter> optionalSerial() {
        return Optional.ofNullable(serial);
    }

    public StringFilter serial() {
        if (serial == null) {
            setSerial(new StringFilter());
        }
        return serial;
    }

    public void setSerial(StringFilter serial) {
        this.serial = serial;
    }

    public StringFilter getConditionNote() {
        return conditionNote;
    }

    public Optional<StringFilter> optionalConditionNote() {
        return Optional.ofNullable(conditionNote);
    }

    public StringFilter conditionNote() {
        if (conditionNote == null) {
            setConditionNote(new StringFilter());
        }
        return conditionNote;
    }

    public void setConditionNote(StringFilter conditionNote) {
        this.conditionNote = conditionNote;
    }

    public EquipmentOperationalStatusFilter getStatus() {
        return status;
    }

    public Optional<EquipmentOperationalStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public EquipmentOperationalStatusFilter status() {
        if (status == null) {
            setStatus(new EquipmentOperationalStatusFilter());
        }
        return status;
    }

    public void setStatus(EquipmentOperationalStatusFilter status) {
        this.status = status;
    }

    public BigDecimalFilter getPurchasePrice() {
        return purchasePrice;
    }

    public Optional<BigDecimalFilter> optionalPurchasePrice() {
        return Optional.ofNullable(purchasePrice);
    }

    public BigDecimalFilter purchasePrice() {
        if (purchasePrice == null) {
            setPurchasePrice(new BigDecimalFilter());
        }
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimalFilter purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDateFilter getCapitalizationDate() {
        return capitalizationDate;
    }

    public Optional<LocalDateFilter> optionalCapitalizationDate() {
        return Optional.ofNullable(capitalizationDate);
    }

    public LocalDateFilter capitalizationDate() {
        if (capitalizationDate == null) {
            setCapitalizationDate(new LocalDateFilter());
        }
        return capitalizationDate;
    }

    public void setCapitalizationDate(LocalDateFilter capitalizationDate) {
        this.capitalizationDate = capitalizationDate;
    }

    public IntegerFilter getDepreciationMonths() {
        return depreciationMonths;
    }

    public Optional<IntegerFilter> optionalDepreciationMonths() {
        return Optional.ofNullable(depreciationMonths);
    }

    public IntegerFilter depreciationMonths() {
        if (depreciationMonths == null) {
            setDepreciationMonths(new IntegerFilter());
        }
        return depreciationMonths;
    }

    public void setDepreciationMonths(IntegerFilter depreciationMonths) {
        this.depreciationMonths = depreciationMonths;
    }

    public BigDecimalFilter getSalvageValue() {
        return salvageValue;
    }

    public Optional<BigDecimalFilter> optionalSalvageValue() {
        return Optional.ofNullable(salvageValue);
    }

    public BigDecimalFilter salvageValue() {
        if (salvageValue == null) {
            setSalvageValue(new BigDecimalFilter());
        }
        return salvageValue;
    }

    public void setSalvageValue(BigDecimalFilter salvageValue) {
        this.salvageValue = salvageValue;
    }

    public BigDecimalFilter getBookValueSnapshot() {
        return bookValueSnapshot;
    }

    public Optional<BigDecimalFilter> optionalBookValueSnapshot() {
        return Optional.ofNullable(bookValueSnapshot);
    }

    public BigDecimalFilter bookValueSnapshot() {
        if (bookValueSnapshot == null) {
            setBookValueSnapshot(new BigDecimalFilter());
        }
        return bookValueSnapshot;
    }

    public void setBookValueSnapshot(BigDecimalFilter bookValueSnapshot) {
        this.bookValueSnapshot = bookValueSnapshot;
    }

    public LongFilter getAssetItemId() {
        return assetItemId;
    }

    public Optional<LongFilter> optionalAssetItemId() {
        return Optional.ofNullable(assetItemId);
    }

    public LongFilter assetItemId() {
        if (assetItemId == null) {
            setAssetItemId(new LongFilter());
        }
        return assetItemId;
    }

    public void setAssetItemId(LongFilter assetItemId) {
        this.assetItemId = assetItemId;
    }

    public LongFilter getSupplierId() {
        return supplierId;
    }

    public Optional<LongFilter> optionalSupplierId() {
        return Optional.ofNullable(supplierId);
    }

    public LongFilter supplierId() {
        if (supplierId == null) {
            setSupplierId(new LongFilter());
        }
        return supplierId;
    }

    public void setSupplierId(LongFilter supplierId) {
        this.supplierId = supplierId;
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
        final EquipmentCriteria that = (EquipmentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(equipmentCode, that.equipmentCode) &&
            Objects.equals(serial, that.serial) &&
            Objects.equals(conditionNote, that.conditionNote) &&
            Objects.equals(status, that.status) &&
            Objects.equals(purchasePrice, that.purchasePrice) &&
            Objects.equals(capitalizationDate, that.capitalizationDate) &&
            Objects.equals(depreciationMonths, that.depreciationMonths) &&
            Objects.equals(salvageValue, that.salvageValue) &&
            Objects.equals(bookValueSnapshot, that.bookValueSnapshot) &&
            Objects.equals(assetItemId, that.assetItemId) &&
            Objects.equals(supplierId, that.supplierId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            equipmentCode,
            serial,
            conditionNote,
            status,
            purchasePrice,
            capitalizationDate,
            depreciationMonths,
            salvageValue,
            bookValueSnapshot,
            assetItemId,
            supplierId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalEquipmentCode().map(f -> "equipmentCode=" + f + ", ").orElse("") +
            optionalSerial().map(f -> "serial=" + f + ", ").orElse("") +
            optionalConditionNote().map(f -> "conditionNote=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalPurchasePrice().map(f -> "purchasePrice=" + f + ", ").orElse("") +
            optionalCapitalizationDate().map(f -> "capitalizationDate=" + f + ", ").orElse("") +
            optionalDepreciationMonths().map(f -> "depreciationMonths=" + f + ", ").orElse("") +
            optionalSalvageValue().map(f -> "salvageValue=" + f + ", ").orElse("") +
            optionalBookValueSnapshot().map(f -> "bookValueSnapshot=" + f + ", ").orElse("") +
            optionalAssetItemId().map(f -> "assetItemId=" + f + ", ").orElse("") +
            optionalSupplierId().map(f -> "supplierId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

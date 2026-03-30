package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Thiết bị — mỗi chiếc một bản ghi, equipment_code duy nhất (vd EQ000001).
 * Khấu hao đường thẳng: nguyên giá, ngày vốn hóa, số tháng, giá trị thu hồi cuối kỳ.
 */
@Entity
@Table(name = "equipment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "equipment_code", length = 20, nullable = false, unique = true)
    private String equipmentCode;

    @Size(max = 100)
    @Column(name = "serial", length = 100)
    private String serial;

    @Size(max = 500)
    @Column(name = "condition_note", length = 500)
    private String conditionNote;

    @Size(max = 150)
    @Column(name = "model_name", length = 150)
    private String modelName;

    @Size(max = 150)
    @Column(name = "brand_name", length = 150)
    private String brandName;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EquipmentOperationalStatus status;

    @DecimalMin(value = "0")
    @Column(name = "purchase_price", precision = 21, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "capitalization_date")
    private LocalDate capitalizationDate;

    @Min(value = 0)
    @Column(name = "depreciation_months")
    private Integer depreciationMonths;

    @DecimalMin(value = "0")
    @Column(name = "salvage_value", precision = 21, scale = 2)
    private BigDecimal salvageValue;

    /**
     * Giá trị còn lại có thể tính server-side; lưu snapshot nếu cần báo cáo
     */
    @DecimalMin(value = "0")
    @Column(name = "book_value_snapshot", precision = 21, scale = 2)
    private BigDecimal bookValueSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Equipment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquipmentCode() {
        return this.equipmentCode;
    }

    public Equipment equipmentCode(String equipmentCode) {
        this.setEquipmentCode(equipmentCode);
        return this;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getSerial() {
        return this.serial;
    }

    public Equipment serial(String serial) {
        this.setSerial(serial);
        return this;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getConditionNote() {
        return this.conditionNote;
    }

    public Equipment conditionNote(String conditionNote) {
        this.setConditionNote(conditionNote);
        return this;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }

    public String getModelName() {
        return this.modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Equipment modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public String getBrandName() {
        return this.brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Equipment brandName(String brandName) {
        this.setBrandName(brandName);
        return this;
    }

    public EquipmentOperationalStatus getStatus() {
        return this.status;
    }

    public Equipment status(EquipmentOperationalStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(EquipmentOperationalStatus status) {
        this.status = status;
    }

    public BigDecimal getPurchasePrice() {
        return this.purchasePrice;
    }

    public Equipment purchasePrice(BigDecimal purchasePrice) {
        this.setPurchasePrice(purchasePrice);
        return this;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getCapitalizationDate() {
        return this.capitalizationDate;
    }

    public Equipment capitalizationDate(LocalDate capitalizationDate) {
        this.setCapitalizationDate(capitalizationDate);
        return this;
    }

    public void setCapitalizationDate(LocalDate capitalizationDate) {
        this.capitalizationDate = capitalizationDate;
    }

    public Integer getDepreciationMonths() {
        return this.depreciationMonths;
    }

    public Equipment depreciationMonths(Integer depreciationMonths) {
        this.setDepreciationMonths(depreciationMonths);
        return this;
    }

    public void setDepreciationMonths(Integer depreciationMonths) {
        this.depreciationMonths = depreciationMonths;
    }

    public BigDecimal getSalvageValue() {
        return this.salvageValue;
    }

    public Equipment salvageValue(BigDecimal salvageValue) {
        this.setSalvageValue(salvageValue);
        return this;
    }

    public void setSalvageValue(BigDecimal salvageValue) {
        this.salvageValue = salvageValue;
    }

    public BigDecimal getBookValueSnapshot() {
        return this.bookValueSnapshot;
    }

    public Equipment bookValueSnapshot(BigDecimal bookValueSnapshot) {
        this.setBookValueSnapshot(bookValueSnapshot);
        return this;
    }

    public void setBookValueSnapshot(BigDecimal bookValueSnapshot) {
        this.bookValueSnapshot = bookValueSnapshot;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public Equipment assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    public Supplier getSupplier() {
        return this.supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Equipment supplier(Supplier supplier) {
        this.setSupplier(supplier);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Equipment)) {
            return false;
        }
        return getId() != null && getId().equals(((Equipment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Equipment{" +
            "id=" + getId() +
            ", equipmentCode='" + getEquipmentCode() + "'" +
            ", serial='" + getSerial() + "'" +
            ", modelName='" + getModelName() + "'" +
            ", brandName='" + getBrandName() + "'" +
            ", conditionNote='" + getConditionNote() + "'" +
            ", status='" + getStatus() + "'" +
            ", purchasePrice=" + getPurchasePrice() +
            ", capitalizationDate='" + getCapitalizationDate() + "'" +
            ", depreciationMonths=" + getDepreciationMonths() +
            ", salvageValue=" + getSalvageValue() +
            ", bookValueSnapshot=" + getBookValueSnapshot() +
            "}";
    }
}

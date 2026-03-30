package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Master tài sản (item): mã TSxxxxxx, tên, loại quản lý thiết bị/vật tư, ĐVT,
 * thiết bị: có khấu hao & serial.
 */
@Entity
@Table(name = "asset_item")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssetItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "management_type", nullable = false)
    private AssetManagementType managementType;

    @Size(max = 20)
    @Column(name = "unit", length = 20)
    private String unit;

    /**
     * Thiết bị: bật khấu hao/serial theo nghiệp vụ
     */
    @NotNull
    @Column(name = "depreciation_enabled", nullable = false)
    private Boolean depreciationEnabled;

    @NotNull
    @Column(name = "serial_tracking_required", nullable = false)
    private Boolean serialTrackingRequired;

    @Size(max = 2000)
    @Column(name = "note", length = 2000)
    private String note;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetGroup" }, allowSetters = true)
    private AssetLine assetLine;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AssetItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public AssetItem code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public AssetItem name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetManagementType getManagementType() {
        return this.managementType;
    }

    public AssetItem managementType(AssetManagementType managementType) {
        this.setManagementType(managementType);
        return this;
    }

    public void setManagementType(AssetManagementType managementType) {
        this.managementType = managementType;
    }

    public String getUnit() {
        return this.unit;
    }

    public AssetItem unit(String unit) {
        this.setUnit(unit);
        return this;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getDepreciationEnabled() {
        return this.depreciationEnabled;
    }

    public AssetItem depreciationEnabled(Boolean depreciationEnabled) {
        this.setDepreciationEnabled(depreciationEnabled);
        return this;
    }

    public void setDepreciationEnabled(Boolean depreciationEnabled) {
        this.depreciationEnabled = depreciationEnabled;
    }

    public Boolean getSerialTrackingRequired() {
        return this.serialTrackingRequired;
    }

    public AssetItem serialTrackingRequired(Boolean serialTrackingRequired) {
        this.setSerialTrackingRequired(serialTrackingRequired);
        return this;
    }

    public void setSerialTrackingRequired(Boolean serialTrackingRequired) {
        this.serialTrackingRequired = serialTrackingRequired;
    }

    public String getNote() {
        return this.note;
    }

    public AssetItem note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getActive() {
        return this.active;
    }

    public AssetItem active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AssetLine getAssetLine() {
        return this.assetLine;
    }

    public void setAssetLine(AssetLine assetLine) {
        this.assetLine = assetLine;
    }

    public AssetItem assetLine(AssetLine assetLine) {
        this.setAssetLine(assetLine);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssetItem)) {
            return false;
        }
        return getId() != null && getId().equals(((AssetItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssetItem{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", managementType='" + getManagementType() + "'" +
            ", unit='" + getUnit() + "'" +
            ", depreciationEnabled='" + getDepreciationEnabled() + "'" +
            ", serialTrackingRequired='" + getSerialTrackingRequired() + "'" +
            ", note='" + getNote() + "'" +
            ", active='" + getActive() + "'" +
            "}";
    }
}

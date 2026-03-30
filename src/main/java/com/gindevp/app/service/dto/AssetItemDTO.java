package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.AssetManagementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.AssetItem} entity.
 */
@Schema(description = "Master tài sản (item): mã TSxxxxxx, tên, loại quản lý thiết bị/vật tư, ĐVT,\nthiết bị: có khấu hao & serial.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AssetItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private AssetManagementType managementType;

    @Size(max = 20)
    private String unit;

    @NotNull
    @Schema(description = "Thiết bị: bật khấu hao/serial theo nghiệp vụ", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean depreciationEnabled;

    @NotNull
    private Boolean serialTrackingRequired;

    @Size(max = 2000)
    private String note;

    @NotNull
    private Boolean active;

    private AssetLineDTO assetLine;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetManagementType getManagementType() {
        return managementType;
    }

    public void setManagementType(AssetManagementType managementType) {
        this.managementType = managementType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getDepreciationEnabled() {
        return depreciationEnabled;
    }

    public void setDepreciationEnabled(Boolean depreciationEnabled) {
        this.depreciationEnabled = depreciationEnabled;
    }

    public Boolean getSerialTrackingRequired() {
        return serialTrackingRequired;
    }

    public void setSerialTrackingRequired(Boolean serialTrackingRequired) {
        this.serialTrackingRequired = serialTrackingRequired;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public AssetLineDTO getAssetLine() {
        return assetLine;
    }

    public void setAssetLine(AssetLineDTO assetLine) {
        this.assetLine = assetLine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssetItemDTO)) {
            return false;
        }

        AssetItemDTO assetItemDTO = (AssetItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, assetItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AssetItemDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", managementType='" + getManagementType() + "'" +
            ", unit='" + getUnit() + "'" +
            ", depreciationEnabled='" + getDepreciationEnabled() + "'" +
            ", serialTrackingRequired='" + getSerialTrackingRequired() + "'" +
            ", note='" + getNote() + "'" +
            ", active='" + getActive() + "'" +
            ", assetLine=" + getAssetLine() +
            "}";
    }
}

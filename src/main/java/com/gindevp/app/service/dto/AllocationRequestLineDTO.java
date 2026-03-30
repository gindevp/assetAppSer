package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.AssetManagementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.AllocationRequestLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AllocationRequestLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer lineNo;

    @NotNull
    private AssetManagementType lineType;

    @Min(value = 1)
    @Schema(description = "Vật tư: số lượng; Thiết bị: có thể 1 và chọn dòng thiết bị")
    private Integer quantity;

    @Size(max = 500)
    private String note;

    private AllocationRequestDTO request;

    private AssetItemDTO assetItem;

    private AssetLineDTO assetLine;

    private EquipmentDTO equipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public AssetManagementType getLineType() {
        return lineType;
    }

    public void setLineType(AssetManagementType lineType) {
        this.lineType = lineType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AllocationRequestDTO getRequest() {
        return request;
    }

    public void setRequest(AllocationRequestDTO request) {
        this.request = request;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
    }

    public AssetLineDTO getAssetLine() {
        return assetLine;
    }

    public void setAssetLine(AssetLineDTO assetLine) {
        this.assetLine = assetLine;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AllocationRequestLineDTO)) {
            return false;
        }

        AllocationRequestLineDTO allocationRequestLineDTO = (AllocationRequestLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, allocationRequestLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AllocationRequestLineDTO{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", lineType='" + getLineType() + "'" +
            ", quantity=" + getQuantity() +
            ", note='" + getNote() + "'" +
            ", request=" + getRequest() +
            ", assetItem=" + getAssetItem() +
            ", assetLine=" + getAssetLine() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

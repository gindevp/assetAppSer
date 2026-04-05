package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.AssetManagementType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

@Schema(description = "Dòng thiết bị hoặc vật tư trên phiếu sửa chữa")
public class RepairRequestLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer lineNo;

    private RepairRequestDTO repairRequest;

    /** DEVICE: cần equipment. CONSUMABLE: cần assetItem + quantity. */
    private AssetManagementType lineType;

    private AssetItemDTO assetItem;

    @Min(value = 1)
    private Integer quantity;

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

    public RepairRequestDTO getRepairRequest() {
        return repairRequest;
    }

    public void setRepairRequest(RepairRequestDTO repairRequest) {
        this.repairRequest = repairRequest;
    }

    public AssetManagementType getLineType() {
        return lineType;
    }

    public void setLineType(AssetManagementType lineType) {
        this.lineType = lineType;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
        if (!(o instanceof RepairRequestLineDTO)) {
            return false;
        }
        RepairRequestLineDTO that = (RepairRequestLineDTO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

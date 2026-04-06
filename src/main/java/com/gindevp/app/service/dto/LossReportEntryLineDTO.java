package com.gindevp.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/** Một dòng trong phiếu báo mất gộp (COMBINED). */
@Schema(description = "Dòng tài sản trong YC báo mất gộp")
public class LossReportEntryLineDTO implements Serializable {

    /** EQUIPMENT | CONSUMABLE */
    private String lineType;

    private Long equipmentId;

    private Long consumableAssignmentId;

    /** Bắt buộc với CONSUMABLE */
    private Integer quantity;

    /** Chỉ khi trả về từ API — id mặt hàng (tiện FE). */
    private Long assetItemId;

    public String getLineType() {
        return lineType;
    }

    public void setLineType(String lineType) {
        this.lineType = lineType;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Long getConsumableAssignmentId() {
        return consumableAssignmentId;
    }

    public void setConsumableAssignmentId(Long consumableAssignmentId) {
        this.consumableAssignmentId = consumableAssignmentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getAssetItemId() {
        return assetItemId;
    }

    public void setAssetItemId(Long assetItemId) {
        this.assetItemId = assetItemId;
    }
}

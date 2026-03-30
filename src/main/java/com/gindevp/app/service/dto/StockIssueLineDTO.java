package com.gindevp.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.StockIssueLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockIssueLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer lineNo;

    @NotNull
    @Min(value = 1)
    @Schema(description = "Vật tư: số lượng xuất; Thiết bị: 1 và bắt buộc equipment", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer quantity;

    @Size(max = 500)
    private String note;

    private StockIssueDTO issue;

    private AssetItemDTO assetItem;

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

    public StockIssueDTO getIssue() {
        return issue;
    }

    public void setIssue(StockIssueDTO issue) {
        this.issue = issue;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
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
        if (!(o instanceof StockIssueLineDTO)) {
            return false;
        }

        StockIssueLineDTO stockIssueLineDTO = (StockIssueLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockIssueLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockIssueLineDTO{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", quantity=" + getQuantity() +
            ", note='" + getNote() + "'" +
            ", issue=" + getIssue() +
            ", assetItem=" + getAssetItem() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

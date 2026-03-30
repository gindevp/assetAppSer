package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.ReturnDisposition;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.ReturnRequestLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnRequestLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer lineNo;

    @NotNull
    private AssetManagementType lineType;

    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private Boolean selected;

    @Size(max = 500)
    private String note;

    private ReturnRequestDTO request;

    private AssetItemDTO assetItem;

    private EquipmentDTO equipment;

    private ReturnDisposition disposition;

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

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReturnRequestDTO getRequest() {
        return request;
    }

    public void setRequest(ReturnRequestDTO request) {
        this.request = request;
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

    public ReturnDisposition getDisposition() {
        return disposition;
    }

    public void setDisposition(ReturnDisposition disposition) {
        this.disposition = disposition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnRequestLineDTO)) {
            return false;
        }

        ReturnRequestLineDTO returnRequestLineDTO = (ReturnRequestLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, returnRequestLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnRequestLineDTO{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", lineType='" + getLineType() + "'" +
            ", quantity=" + getQuantity() +
            ", selected='" + getSelected() + "'" +
            ", note='" + getNote() + "'" +
            ", request=" + getRequest() +
            ", assetItem=" + getAssetItem() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

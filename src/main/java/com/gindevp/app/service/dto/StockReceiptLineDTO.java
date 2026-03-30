package com.gindevp.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.StockReceiptLine} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockReceiptLineDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer lineNo;

    @NotNull
    @Min(value = 1)
    @Schema(
        description = "Với thiết bị: số lượng = số bản ghi Equipment tạo ra; với vật tư: cộng tồn",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer quantity;

    @DecimalMin(value = "0")
    private BigDecimal unitPrice;

    @Size(max = 500)
    private String note;

    private StockReceiptDTO receipt;

    private AssetItemDTO assetItem;

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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StockReceiptDTO getReceipt() {
        return receipt;
    }

    public void setReceipt(StockReceiptDTO receipt) {
        this.receipt = receipt;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockReceiptLineDTO)) {
            return false;
        }

        StockReceiptLineDTO stockReceiptLineDTO = (StockReceiptLineDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockReceiptLineDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockReceiptLineDTO{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", note='" + getNote() + "'" +
            ", receipt=" + getReceipt() +
            ", assetItem=" + getAssetItem() +
            "}";
    }
}

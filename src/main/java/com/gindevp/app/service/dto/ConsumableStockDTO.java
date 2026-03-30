package com.gindevp.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.ConsumableStock} entity.
 */
@Schema(description = "Tồn kho vật tư theo master — không tách từng cái")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConsumableStockDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer quantityOnHand;

    @NotNull
    @Min(value = 0)
    private Integer quantityIssued;

    @Size(max = 500)
    private String note;

    private AssetItemDTO assetItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

    public Integer getQuantityIssued() {
        return quantityIssued;
    }

    public void setQuantityIssued(Integer quantityIssued) {
        this.quantityIssued = quantityIssued;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
        if (!(o instanceof ConsumableStockDTO)) {
            return false;
        }

        ConsumableStockDTO consumableStockDTO = (ConsumableStockDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, consumableStockDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConsumableStockDTO{" +
            "id=" + getId() +
            ", quantityOnHand=" + getQuantityOnHand() +
            ", quantityIssued=" + getQuantityIssued() +
            ", note='" + getNote() + "'" +
            ", assetItem=" + getAssetItem() +
            "}";
    }
}

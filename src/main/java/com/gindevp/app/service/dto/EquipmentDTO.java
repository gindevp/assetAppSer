package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.EquipmentOperationalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.Equipment} entity.
 */
@Schema(
    description = "Thiết bị — mỗi chiếc một bản ghi, equipment_code duy nhất (vd EQ000001).\nKhấu hao đường thẳng: nguyên giá, ngày vốn hóa, số tháng, giá trị thu hồi cuối kỳ."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    @Pattern(regexp = "^EQ\\d{6}$", message = "Mã thiết bị: EQ + đúng 6 chữ số (vd EQ000001)")
    private String equipmentCode;

    @Size(max = 100)
    private String serial;

    @Size(max = 500)
    private String conditionNote;

    @Size(max = 150)
    private String modelName;

    @Size(max = 150)
    private String brandName;

    @NotNull
    private EquipmentOperationalStatus status;

    @DecimalMin(value = "0")
    private BigDecimal purchasePrice;

    private LocalDate capitalizationDate;

    @Min(value = 0)
    private Integer depreciationMonths;

    @DecimalMin(value = "0")
    private BigDecimal salvageValue;

    @DecimalMin(value = "0")
    @Schema(description = "Giá trị còn lại có thể tính server-side; lưu snapshot nếu cần báo cáo")
    private BigDecimal bookValueSnapshot;

    private AssetItemDTO assetItem;

    private SupplierDTO supplier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public void setConditionNote(String conditionNote) {
        this.conditionNote = conditionNote;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public EquipmentOperationalStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentOperationalStatus status) {
        this.status = status;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public LocalDate getCapitalizationDate() {
        return capitalizationDate;
    }

    public void setCapitalizationDate(LocalDate capitalizationDate) {
        this.capitalizationDate = capitalizationDate;
    }

    public Integer getDepreciationMonths() {
        return depreciationMonths;
    }

    public void setDepreciationMonths(Integer depreciationMonths) {
        this.depreciationMonths = depreciationMonths;
    }

    public BigDecimal getSalvageValue() {
        return salvageValue;
    }

    public void setSalvageValue(BigDecimal salvageValue) {
        this.salvageValue = salvageValue;
    }

    public BigDecimal getBookValueSnapshot() {
        return bookValueSnapshot;
    }

    public void setBookValueSnapshot(BigDecimal bookValueSnapshot) {
        this.bookValueSnapshot = bookValueSnapshot;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
    }

    public SupplierDTO getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDTO supplier) {
        this.supplier = supplier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EquipmentDTO)) {
            return false;
        }

        EquipmentDTO equipmentDTO = (EquipmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentDTO{" +
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
            ", assetItem=" + getAssetItem() +
            ", supplier=" + getSupplier() +
            "}";
    }
}

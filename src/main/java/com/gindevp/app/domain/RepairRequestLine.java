package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * Dòng thiết bị hoặc vật tư trên phiếu yêu cầu sửa chữa (một phiếu có thể nhiều dòng).
 */
@Entity
@Table(name = "repair_request_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RepairRequestLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "line_no", nullable = false)
    private Integer lineNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "requester", "lines" }, allowSetters = true)
    private RepairRequest repairRequest;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "line_type", nullable = false)
    private AssetManagementType lineType = AssetManagementType.DEVICE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    @Min(value = 1)
    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetItem", "supplier" }, allowSetters = true)
    private Equipment equipment;

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

    public RepairRequest getRepairRequest() {
        return repairRequest;
    }

    public void setRepairRequest(RepairRequest repairRequest) {
        this.repairRequest = repairRequest;
    }

    public AssetManagementType getLineType() {
        return lineType;
    }

    public void setLineType(AssetManagementType lineType) {
        this.lineType = lineType;
    }

    public AssetItem getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepairRequestLine)) {
            return false;
        }
        return getId() != null && getId().equals(((RepairRequestLine) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

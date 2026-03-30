package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A AllocationRequestLine.
 */
@Entity
@Table(name = "allocation_request_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AllocationRequestLine implements Serializable {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "line_type", nullable = false)
    private AssetManagementType lineType;

    /**
     * Vật tư: số lượng; Thiết bị: có thể 1 và chọn dòng thiết bị
     */
    @Min(value = 1)
    @Column(name = "quantity")
    private Integer quantity;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "requester" }, allowSetters = true)
    private AllocationRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetGroup" }, allowSetters = true)
    private AssetLine assetLine;

    /**
     * Thiết bị cụ thể chọn khi duyệt (tồn kho).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetItem", "supplier" }, allowSetters = true)
    private Equipment equipment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AllocationRequestLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return this.lineNo;
    }

    public AllocationRequestLine lineNo(Integer lineNo) {
        this.setLineNo(lineNo);
        return this;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public AssetManagementType getLineType() {
        return this.lineType;
    }

    public AllocationRequestLine lineType(AssetManagementType lineType) {
        this.setLineType(lineType);
        return this;
    }

    public void setLineType(AssetManagementType lineType) {
        this.lineType = lineType;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public AllocationRequestLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return this.note;
    }

    public AllocationRequestLine note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AllocationRequest getRequest() {
        return this.request;
    }

    public void setRequest(AllocationRequest allocationRequest) {
        this.request = allocationRequest;
    }

    public AllocationRequestLine request(AllocationRequest allocationRequest) {
        this.setRequest(allocationRequest);
        return this;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public AllocationRequestLine assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    public AssetLine getAssetLine() {
        return this.assetLine;
    }

    public void setAssetLine(AssetLine assetLine) {
        this.assetLine = assetLine;
    }

    public AllocationRequestLine assetLine(AssetLine assetLine) {
        this.setAssetLine(assetLine);
        return this;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public AllocationRequestLine equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AllocationRequestLine)) {
            return false;
        }
        return getId() != null && getId().equals(((AllocationRequestLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AllocationRequestLine{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", lineType='" + getLineType() + "'" +
            ", quantity=" + getQuantity() +
            ", note='" + getNote() + "'" +
            "}";
    }
}

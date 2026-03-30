package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.AssetManagementType;
import com.gindevp.app.domain.enumeration.ReturnDisposition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A ReturnRequestLine.
 */
@Entity
@Table(name = "return_request_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnRequestLine implements Serializable {

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

    @Min(value = 1)
    @Column(name = "quantity")
    private Integer quantity;

    @NotNull
    @Column(name = "selected", nullable = false)
    private Boolean selected;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "requester" }, allowSetters = true)
    private ReturnRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetItem", "supplier" }, allowSetters = true)
    private Equipment equipment;

    /**
     * Hướng xử lý sau thu hồi (kho / sửa / hỏng / mất). Mặc định TO_STOCK nếu null.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "disposition")
    private ReturnDisposition disposition;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ReturnRequestLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return this.lineNo;
    }

    public ReturnRequestLine lineNo(Integer lineNo) {
        this.setLineNo(lineNo);
        return this;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public AssetManagementType getLineType() {
        return this.lineType;
    }

    public ReturnRequestLine lineType(AssetManagementType lineType) {
        this.setLineType(lineType);
        return this;
    }

    public void setLineType(AssetManagementType lineType) {
        this.lineType = lineType;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ReturnRequestLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getSelected() {
        return this.selected;
    }

    public ReturnRequestLine selected(Boolean selected) {
        this.setSelected(selected);
        return this;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getNote() {
        return this.note;
    }

    public ReturnRequestLine note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ReturnRequest getRequest() {
        return this.request;
    }

    public void setRequest(ReturnRequest returnRequest) {
        this.request = returnRequest;
    }

    public ReturnRequestLine request(ReturnRequest returnRequest) {
        this.setRequest(returnRequest);
        return this;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public ReturnRequestLine assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public ReturnRequestLine equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    public ReturnDisposition getDisposition() {
        return disposition;
    }

    public void setDisposition(ReturnDisposition disposition) {
        this.disposition = disposition;
    }

    public ReturnRequestLine disposition(ReturnDisposition disposition) {
        this.setDisposition(disposition);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReturnRequestLine)) {
            return false;
        }
        return getId() != null && getId().equals(((ReturnRequestLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnRequestLine{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", lineType='" + getLineType() + "'" +
            ", quantity=" + getQuantity() +
            ", selected='" + getSelected() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

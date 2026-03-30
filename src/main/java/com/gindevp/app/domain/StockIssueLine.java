package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;

/**
 * A StockIssueLine.
 */
@Entity
@Table(name = "stock_issue_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockIssueLine implements Serializable {

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

    /**
     * Vật tư: số lượng xuất; Thiết bị: 1 và bắt buộc equipment
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "issue", "employee", "department", "location" }, allowSetters = true)
    private StockIssue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetItem", "supplier" }, allowSetters = true)
    private Equipment equipment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockIssueLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return this.lineNo;
    }

    public StockIssueLine lineNo(Integer lineNo) {
        this.setLineNo(lineNo);
        return this;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public StockIssueLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getNote() {
        return this.note;
    }

    public StockIssueLine note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StockIssue getIssue() {
        return this.issue;
    }

    public void setIssue(StockIssue stockIssue) {
        this.issue = stockIssue;
    }

    public StockIssueLine issue(StockIssue stockIssue) {
        this.setIssue(stockIssue);
        return this;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public StockIssueLine assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public StockIssueLine equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockIssueLine)) {
            return false;
        }
        return getId() != null && getId().equals(((StockIssueLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockIssueLine{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", quantity=" + getQuantity() +
            ", note='" + getNote() + "'" +
            "}";
    }
}

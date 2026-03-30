package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A StockReceiptLine.
 */
@Entity
@Table(name = "stock_receipt_line")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockReceiptLine implements Serializable {

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
     * Với thiết bị: số lượng = số bản ghi Equipment tạo ra; với vật tư: cộng tồn
     */
    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0")
    @Column(name = "unit_price", precision = 21, scale = 2)
    private BigDecimal unitPrice;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    private StockReceipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockReceiptLine id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLineNo() {
        return this.lineNo;
    }

    public StockReceiptLine lineNo(Integer lineNo) {
        this.setLineNo(lineNo);
        return this;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public StockReceiptLine quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public StockReceiptLine unitPrice(BigDecimal unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getNote() {
        return this.note;
    }

    public StockReceiptLine note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public StockReceipt getReceipt() {
        return this.receipt;
    }

    public void setReceipt(StockReceipt stockReceipt) {
        this.receipt = stockReceipt;
    }

    public StockReceiptLine receipt(StockReceipt stockReceipt) {
        this.setReceipt(stockReceipt);
        return this;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public StockReceiptLine assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockReceiptLine)) {
            return false;
        }
        return getId() != null && getId().equals(((StockReceiptLine) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockReceiptLine{" +
            "id=" + getId() +
            ", lineNo=" + getLineNo() +
            ", quantity=" + getQuantity() +
            ", unitPrice=" + getUnitPrice() +
            ", note='" + getNote() + "'" +
            "}";
    }
}

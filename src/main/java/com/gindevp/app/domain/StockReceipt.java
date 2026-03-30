package com.gindevp.app.domain;

import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.domain.enumeration.StockReceiptSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Phiếu nhập kho
 */
@Entity
@Table(name = "stock_receipt")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 20)
    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @NotNull
    @Column(name = "receipt_date", nullable = false)
    private LocalDate receiptDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private StockReceiptSource source;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @Size(max = 2000)
    @Column(name = "note", length = 2000)
    private String note;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockReceipt id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public StockReceipt code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getReceiptDate() {
        return this.receiptDate;
    }

    public StockReceipt receiptDate(LocalDate receiptDate) {
        this.setReceiptDate(receiptDate);
        return this;
    }

    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }

    public StockReceiptSource getSource() {
        return this.source;
    }

    public StockReceipt source(StockReceiptSource source) {
        this.setSource(source);
        return this;
    }

    public void setSource(StockReceiptSource source) {
        this.source = source;
    }

    public DocumentStatus getStatus() {
        return this.status;
    }

    public StockReceipt status(DocumentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return this.note;
    }

    public StockReceipt note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockReceipt)) {
            return false;
        }
        return getId() != null && getId().equals(((StockReceipt) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockReceipt{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", receiptDate='" + getReceiptDate() + "'" +
            ", source='" + getSource() + "'" +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.domain.enumeration.StockReceiptSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.StockReceipt} entity.
 */
@Schema(description = "Phiếu nhập kho")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockReceiptDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    private LocalDate receiptDate;

    @NotNull
    private StockReceiptSource source;

    @NotNull
    private DocumentStatus status;

    @Size(max = 2000)
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }

    public StockReceiptSource getSource() {
        return source;
    }

    public void setSource(StockReceiptSource source) {
        this.source = source;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockReceiptDTO)) {
            return false;
        }

        StockReceiptDTO stockReceiptDTO = (StockReceiptDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockReceiptDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockReceiptDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", receiptDate='" + getReceiptDate() + "'" +
            ", source='" + getSource() + "'" +
            ", status='" + getStatus() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

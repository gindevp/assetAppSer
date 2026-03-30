package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.DocumentStatus;
import com.gindevp.app.domain.enumeration.StockReceiptSource;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.StockReceipt} entity. This class is used
 * in {@link com.gindevp.app.web.rest.StockReceiptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-receipts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockReceiptCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StockReceiptSource
     */
    public static class StockReceiptSourceFilter extends Filter<StockReceiptSource> {

        public StockReceiptSourceFilter() {}

        public StockReceiptSourceFilter(StockReceiptSourceFilter filter) {
            super(filter);
        }

        @Override
        public StockReceiptSourceFilter copy() {
            return new StockReceiptSourceFilter(this);
        }
    }

    /**
     * Class for filtering DocumentStatus
     */
    public static class DocumentStatusFilter extends Filter<DocumentStatus> {

        public DocumentStatusFilter() {}

        public DocumentStatusFilter(DocumentStatusFilter filter) {
            super(filter);
        }

        @Override
        public DocumentStatusFilter copy() {
            return new DocumentStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LocalDateFilter receiptDate;

    private StockReceiptSourceFilter source;

    private DocumentStatusFilter status;

    private StringFilter note;

    private Boolean distinct;

    public StockReceiptCriteria() {}

    public StockReceiptCriteria(StockReceiptCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.receiptDate = other.optionalReceiptDate().map(LocalDateFilter::copy).orElse(null);
        this.source = other.optionalSource().map(StockReceiptSourceFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DocumentStatusFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StockReceiptCriteria copy() {
        return new StockReceiptCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public LocalDateFilter getReceiptDate() {
        return receiptDate;
    }

    public Optional<LocalDateFilter> optionalReceiptDate() {
        return Optional.ofNullable(receiptDate);
    }

    public LocalDateFilter receiptDate() {
        if (receiptDate == null) {
            setReceiptDate(new LocalDateFilter());
        }
        return receiptDate;
    }

    public void setReceiptDate(LocalDateFilter receiptDate) {
        this.receiptDate = receiptDate;
    }

    public StockReceiptSourceFilter getSource() {
        return source;
    }

    public Optional<StockReceiptSourceFilter> optionalSource() {
        return Optional.ofNullable(source);
    }

    public StockReceiptSourceFilter source() {
        if (source == null) {
            setSource(new StockReceiptSourceFilter());
        }
        return source;
    }

    public void setSource(StockReceiptSourceFilter source) {
        this.source = source;
    }

    public DocumentStatusFilter getStatus() {
        return status;
    }

    public Optional<DocumentStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public DocumentStatusFilter status() {
        if (status == null) {
            setStatus(new DocumentStatusFilter());
        }
        return status;
    }

    public void setStatus(DocumentStatusFilter status) {
        this.status = status;
    }

    public StringFilter getNote() {
        return note;
    }

    public Optional<StringFilter> optionalNote() {
        return Optional.ofNullable(note);
    }

    public StringFilter note() {
        if (note == null) {
            setNote(new StringFilter());
        }
        return note;
    }

    public void setNote(StringFilter note) {
        this.note = note;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StockReceiptCriteria that = (StockReceiptCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(receiptDate, that.receiptDate) &&
            Objects.equals(source, that.source) &&
            Objects.equals(status, that.status) &&
            Objects.equals(note, that.note) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, receiptDate, source, status, note, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockReceiptCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalReceiptDate().map(f -> "receiptDate=" + f + ", ").orElse("") +
            optionalSource().map(f -> "source=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

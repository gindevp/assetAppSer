package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.ReturnRequestStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.ReturnRequest} entity. This class is used
 * in {@link com.gindevp.app.web.rest.ReturnRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /return-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ReturnRequestCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ReturnRequestStatus
     */
    public static class ReturnRequestStatusFilter extends Filter<ReturnRequestStatus> {

        public ReturnRequestStatusFilter() {}

        public ReturnRequestStatusFilter(ReturnRequestStatusFilter filter) {
            super(filter);
        }

        @Override
        public ReturnRequestStatusFilter copy() {
            return new ReturnRequestStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private InstantFilter requestDate;

    private StringFilter note;

    private ReturnRequestStatusFilter status;

    private LongFilter requesterId;

    private Boolean distinct;

    public ReturnRequestCriteria() {}

    public ReturnRequestCriteria(ReturnRequestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.requestDate = other.optionalRequestDate().map(InstantFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ReturnRequestStatusFilter::copy).orElse(null);
        this.requesterId = other.optionalRequesterId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ReturnRequestCriteria copy() {
        return new ReturnRequestCriteria(this);
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

    public InstantFilter getRequestDate() {
        return requestDate;
    }

    public Optional<InstantFilter> optionalRequestDate() {
        return Optional.ofNullable(requestDate);
    }

    public InstantFilter requestDate() {
        if (requestDate == null) {
            setRequestDate(new InstantFilter());
        }
        return requestDate;
    }

    public void setRequestDate(InstantFilter requestDate) {
        this.requestDate = requestDate;
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

    public ReturnRequestStatusFilter getStatus() {
        return status;
    }

    public Optional<ReturnRequestStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ReturnRequestStatusFilter status() {
        if (status == null) {
            setStatus(new ReturnRequestStatusFilter());
        }
        return status;
    }

    public void setStatus(ReturnRequestStatusFilter status) {
        this.status = status;
    }

    public LongFilter getRequesterId() {
        return requesterId;
    }

    public Optional<LongFilter> optionalRequesterId() {
        return Optional.ofNullable(requesterId);
    }

    public LongFilter requesterId() {
        if (requesterId == null) {
            setRequesterId(new LongFilter());
        }
        return requesterId;
    }

    public void setRequesterId(LongFilter requesterId) {
        this.requesterId = requesterId;
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
        final ReturnRequestCriteria that = (ReturnRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(requestDate, that.requestDate) &&
            Objects.equals(note, that.note) &&
            Objects.equals(status, that.status) &&
            Objects.equals(requesterId, that.requesterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, requestDate, note, status, requesterId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ReturnRequestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalRequestDate().map(f -> "requestDate=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalRequesterId().map(f -> "requesterId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

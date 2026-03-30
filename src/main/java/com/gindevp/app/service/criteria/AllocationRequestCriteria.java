package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.AllocationRequestStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.AllocationRequest} entity. This class is used
 * in {@link com.gindevp.app.web.rest.AllocationRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /allocation-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AllocationRequestCriteria implements Serializable, Criteria {

    /**
     * Class for filtering AllocationRequestStatus
     */
    public static class AllocationRequestStatusFilter extends Filter<AllocationRequestStatus> {

        public AllocationRequestStatusFilter() {}

        public AllocationRequestStatusFilter(AllocationRequestStatusFilter filter) {
            super(filter);
        }

        @Override
        public AllocationRequestStatusFilter copy() {
            return new AllocationRequestStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private InstantFilter requestDate;

    private StringFilter reason;

    private AllocationRequestStatusFilter status;

    private StringFilter beneficiaryNote;

    private LongFilter requesterId;

    private Boolean distinct;

    public AllocationRequestCriteria() {}

    public AllocationRequestCriteria(AllocationRequestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.requestDate = other.optionalRequestDate().map(InstantFilter::copy).orElse(null);
        this.reason = other.optionalReason().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(AllocationRequestStatusFilter::copy).orElse(null);
        this.beneficiaryNote = other.optionalBeneficiaryNote().map(StringFilter::copy).orElse(null);
        this.requesterId = other.optionalRequesterId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AllocationRequestCriteria copy() {
        return new AllocationRequestCriteria(this);
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

    public StringFilter getReason() {
        return reason;
    }

    public Optional<StringFilter> optionalReason() {
        return Optional.ofNullable(reason);
    }

    public StringFilter reason() {
        if (reason == null) {
            setReason(new StringFilter());
        }
        return reason;
    }

    public void setReason(StringFilter reason) {
        this.reason = reason;
    }

    public AllocationRequestStatusFilter getStatus() {
        return status;
    }

    public Optional<AllocationRequestStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public AllocationRequestStatusFilter status() {
        if (status == null) {
            setStatus(new AllocationRequestStatusFilter());
        }
        return status;
    }

    public void setStatus(AllocationRequestStatusFilter status) {
        this.status = status;
    }

    public StringFilter getBeneficiaryNote() {
        return beneficiaryNote;
    }

    public Optional<StringFilter> optionalBeneficiaryNote() {
        return Optional.ofNullable(beneficiaryNote);
    }

    public StringFilter beneficiaryNote() {
        if (beneficiaryNote == null) {
            setBeneficiaryNote(new StringFilter());
        }
        return beneficiaryNote;
    }

    public void setBeneficiaryNote(StringFilter beneficiaryNote) {
        this.beneficiaryNote = beneficiaryNote;
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
        final AllocationRequestCriteria that = (AllocationRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(requestDate, that.requestDate) &&
            Objects.equals(reason, that.reason) &&
            Objects.equals(status, that.status) &&
            Objects.equals(beneficiaryNote, that.beneficiaryNote) &&
            Objects.equals(requesterId, that.requesterId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, requestDate, reason, status, beneficiaryNote, requesterId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AllocationRequestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalRequestDate().map(f -> "requestDate=" + f + ", ").orElse("") +
            optionalReason().map(f -> "reason=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalBeneficiaryNote().map(f -> "beneficiaryNote=" + f + ", ").orElse("") +
            optionalRequesterId().map(f -> "requesterId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

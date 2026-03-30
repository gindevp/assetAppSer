package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.RepairRequest} entity. This class is used
 * in {@link com.gindevp.app.web.rest.RepairRequestResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /repair-requests?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RepairRequestCriteria implements Serializable, Criteria {

    /**
     * Class for filtering RepairRequestStatus
     */
    public static class RepairRequestStatusFilter extends Filter<RepairRequestStatus> {

        public RepairRequestStatusFilter() {}

        public RepairRequestStatusFilter(RepairRequestStatusFilter filter) {
            super(filter);
        }

        @Override
        public RepairRequestStatusFilter copy() {
            return new RepairRequestStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private InstantFilter requestDate;

    private StringFilter problemCategory;

    private StringFilter description;

    private RepairRequestStatusFilter status;

    private StringFilter resolutionNote;

    private LongFilter requesterId;

    private LongFilter equipmentId;

    private Boolean distinct;

    public RepairRequestCriteria() {}

    public RepairRequestCriteria(RepairRequestCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.requestDate = other.optionalRequestDate().map(InstantFilter::copy).orElse(null);
        this.problemCategory = other.optionalProblemCategory().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(RepairRequestStatusFilter::copy).orElse(null);
        this.resolutionNote = other.optionalResolutionNote().map(StringFilter::copy).orElse(null);
        this.requesterId = other.optionalRequesterId().map(LongFilter::copy).orElse(null);
        this.equipmentId = other.optionalEquipmentId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public RepairRequestCriteria copy() {
        return new RepairRequestCriteria(this);
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

    public StringFilter getProblemCategory() {
        return problemCategory;
    }

    public Optional<StringFilter> optionalProblemCategory() {
        return Optional.ofNullable(problemCategory);
    }

    public StringFilter problemCategory() {
        if (problemCategory == null) {
            setProblemCategory(new StringFilter());
        }
        return problemCategory;
    }

    public void setProblemCategory(StringFilter problemCategory) {
        this.problemCategory = problemCategory;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public RepairRequestStatusFilter getStatus() {
        return status;
    }

    public Optional<RepairRequestStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public RepairRequestStatusFilter status() {
        if (status == null) {
            setStatus(new RepairRequestStatusFilter());
        }
        return status;
    }

    public void setStatus(RepairRequestStatusFilter status) {
        this.status = status;
    }

    public StringFilter getResolutionNote() {
        return resolutionNote;
    }

    public Optional<StringFilter> optionalResolutionNote() {
        return Optional.ofNullable(resolutionNote);
    }

    public StringFilter resolutionNote() {
        if (resolutionNote == null) {
            setResolutionNote(new StringFilter());
        }
        return resolutionNote;
    }

    public void setResolutionNote(StringFilter resolutionNote) {
        this.resolutionNote = resolutionNote;
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

    public LongFilter getEquipmentId() {
        return equipmentId;
    }

    public Optional<LongFilter> optionalEquipmentId() {
        return Optional.ofNullable(equipmentId);
    }

    public LongFilter equipmentId() {
        if (equipmentId == null) {
            setEquipmentId(new LongFilter());
        }
        return equipmentId;
    }

    public void setEquipmentId(LongFilter equipmentId) {
        this.equipmentId = equipmentId;
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
        final RepairRequestCriteria that = (RepairRequestCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(requestDate, that.requestDate) &&
            Objects.equals(problemCategory, that.problemCategory) &&
            Objects.equals(description, that.description) &&
            Objects.equals(status, that.status) &&
            Objects.equals(resolutionNote, that.resolutionNote) &&
            Objects.equals(requesterId, that.requesterId) &&
            Objects.equals(equipmentId, that.equipmentId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            code,
            requestDate,
            problemCategory,
            description,
            status,
            resolutionNote,
            requesterId,
            equipmentId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RepairRequestCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalRequestDate().map(f -> "requestDate=" + f + ", ").orElse("") +
            optionalProblemCategory().map(f -> "problemCategory=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalResolutionNote().map(f -> "resolutionNote=" + f + ", ").orElse("") +
            optionalRequesterId().map(f -> "requesterId=" + f + ", ").orElse("") +
            optionalEquipmentId().map(f -> "equipmentId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

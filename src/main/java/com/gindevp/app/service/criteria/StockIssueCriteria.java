package com.gindevp.app.service.criteria;

import com.gindevp.app.domain.enumeration.AssigneeType;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.gindevp.app.domain.StockIssue} entity. This class is used
 * in {@link com.gindevp.app.web.rest.StockIssueResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /stock-issues?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockIssueCriteria implements Serializable, Criteria {

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

    /**
     * Class for filtering AssigneeType
     */
    public static class AssigneeTypeFilter extends Filter<AssigneeType> {

        public AssigneeTypeFilter() {}

        public AssigneeTypeFilter(AssigneeTypeFilter filter) {
            super(filter);
        }

        @Override
        public AssigneeTypeFilter copy() {
            return new AssigneeTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private LocalDateFilter issueDate;

    private DocumentStatusFilter status;

    private AssigneeTypeFilter assigneeType;

    private StringFilter note;

    private LongFilter issueId;

    private LongFilter employeeId;

    private LongFilter departmentId;

    private LongFilter locationId;

    private Boolean distinct;

    public StockIssueCriteria() {}

    public StockIssueCriteria(StockIssueCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.issueDate = other.optionalIssueDate().map(LocalDateFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(DocumentStatusFilter::copy).orElse(null);
        this.assigneeType = other.optionalAssigneeType().map(AssigneeTypeFilter::copy).orElse(null);
        this.note = other.optionalNote().map(StringFilter::copy).orElse(null);
        this.issueId = other.optionalIssueId().map(LongFilter::copy).orElse(null);
        this.employeeId = other.optionalEmployeeId().map(LongFilter::copy).orElse(null);
        this.departmentId = other.optionalDepartmentId().map(LongFilter::copy).orElse(null);
        this.locationId = other.optionalLocationId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public StockIssueCriteria copy() {
        return new StockIssueCriteria(this);
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

    public LocalDateFilter getIssueDate() {
        return issueDate;
    }

    public Optional<LocalDateFilter> optionalIssueDate() {
        return Optional.ofNullable(issueDate);
    }

    public LocalDateFilter issueDate() {
        if (issueDate == null) {
            setIssueDate(new LocalDateFilter());
        }
        return issueDate;
    }

    public void setIssueDate(LocalDateFilter issueDate) {
        this.issueDate = issueDate;
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

    public AssigneeTypeFilter getAssigneeType() {
        return assigneeType;
    }

    public Optional<AssigneeTypeFilter> optionalAssigneeType() {
        return Optional.ofNullable(assigneeType);
    }

    public AssigneeTypeFilter assigneeType() {
        if (assigneeType == null) {
            setAssigneeType(new AssigneeTypeFilter());
        }
        return assigneeType;
    }

    public void setAssigneeType(AssigneeTypeFilter assigneeType) {
        this.assigneeType = assigneeType;
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

    public LongFilter getIssueId() {
        return issueId;
    }

    public Optional<LongFilter> optionalIssueId() {
        return Optional.ofNullable(issueId);
    }

    public LongFilter issueId() {
        if (issueId == null) {
            setIssueId(new LongFilter());
        }
        return issueId;
    }

    public void setIssueId(LongFilter issueId) {
        this.issueId = issueId;
    }

    public LongFilter getEmployeeId() {
        return employeeId;
    }

    public Optional<LongFilter> optionalEmployeeId() {
        return Optional.ofNullable(employeeId);
    }

    public LongFilter employeeId() {
        if (employeeId == null) {
            setEmployeeId(new LongFilter());
        }
        return employeeId;
    }

    public void setEmployeeId(LongFilter employeeId) {
        this.employeeId = employeeId;
    }

    public LongFilter getDepartmentId() {
        return departmentId;
    }

    public Optional<LongFilter> optionalDepartmentId() {
        return Optional.ofNullable(departmentId);
    }

    public LongFilter departmentId() {
        if (departmentId == null) {
            setDepartmentId(new LongFilter());
        }
        return departmentId;
    }

    public void setDepartmentId(LongFilter departmentId) {
        this.departmentId = departmentId;
    }

    public LongFilter getLocationId() {
        return locationId;
    }

    public Optional<LongFilter> optionalLocationId() {
        return Optional.ofNullable(locationId);
    }

    public LongFilter locationId() {
        if (locationId == null) {
            setLocationId(new LongFilter());
        }
        return locationId;
    }

    public void setLocationId(LongFilter locationId) {
        this.locationId = locationId;
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
        final StockIssueCriteria that = (StockIssueCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(issueDate, that.issueDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(assigneeType, that.assigneeType) &&
            Objects.equals(note, that.note) &&
            Objects.equals(issueId, that.issueId) &&
            Objects.equals(employeeId, that.employeeId) &&
            Objects.equals(departmentId, that.departmentId) &&
            Objects.equals(locationId, that.locationId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, issueDate, status, assigneeType, note, issueId, employeeId, departmentId, locationId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockIssueCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalIssueDate().map(f -> "issueDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalAssigneeType().map(f -> "assigneeType=" + f + ", ").orElse("") +
            optionalNote().map(f -> "note=" + f + ", ").orElse("") +
            optionalIssueId().map(f -> "issueId=" + f + ", ").orElse("") +
            optionalEmployeeId().map(f -> "employeeId=" + f + ", ").orElse("") +
            optionalDepartmentId().map(f -> "departmentId=" + f + ", ").orElse("") +
            optionalLocationId().map(f -> "locationId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}

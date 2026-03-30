package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.AssigneeType;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.StockIssue} entity.
 */
@Schema(description = "Phiếu xuất / cấp phát")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockIssueDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private DocumentStatus status;

    @NotNull
    private AssigneeType assigneeType;

    @Size(max = 2000)
    private String note;

    @Schema(description = "nullable: chỉ khi assigneeType = EMPLOYEE")
    private EmployeeDTO employee;

    private DepartmentDTO department;

    private LocationDTO location;

    @Schema(description = "YC cấp phát nếu phiếu xuất được tạo từ luồng YC")
    private Long allocationRequestId;

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

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public AssigneeType getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(AssigneeType assigneeType) {
        this.assigneeType = assigneeType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public EmployeeDTO getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeDTO employee) {
        this.employee = employee;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public Long getAllocationRequestId() {
        return allocationRequestId;
    }

    public void setAllocationRequestId(Long allocationRequestId) {
        this.allocationRequestId = allocationRequestId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockIssueDTO)) {
            return false;
        }

        StockIssueDTO stockIssueDTO = (StockIssueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, stockIssueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockIssueDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", issueDate='" + getIssueDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", assigneeType='" + getAssigneeType() + "'" +
            ", note='" + getNote() + "'" +
            ", employee=" + getEmployee() +
            ", department=" + getDepartment() +
            ", location=" + getLocation() +
            "}";
    }
}

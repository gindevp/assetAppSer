package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.AllocationRequestStatus;
import com.gindevp.app.domain.enumeration.AssigneeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.AllocationRequest} entity.
 */
@Schema(description = "Yêu cầu cấp phát")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AllocationRequestDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    private Instant requestDate;

    @Size(max = 2000)
    private String reason;

    @Size(max = 2000)
    @Schema(description = "Ghi chú / link; có thể chứa dòng FILE:url sau khi upload")
    private String attachmentNote;

    @Size(max = 2000)
    @Schema(description = "Lý do từ chối (khi từ chối yêu cầu)")
    private String rejectionReason;

    @NotNull
    private AllocationRequestStatus status;

    @Size(max = 500)
    @Schema(description = "Đối tượng nhận (người khác / phòng / vị trí) — bind theo assigneeType")
    private String beneficiaryNote;

    private AssigneeType assigneeType;

    private EmployeeDTO beneficiaryEmployee;

    private DepartmentDTO beneficiaryDepartment;

    private LocationDTO beneficiaryLocation;

    private EmployeeDTO requester;

    @Schema(description = "Phiếu xuất gắn với YC (nếu đã tạo)")
    private Long stockIssueId;

    @Schema(description = "Mã phiếu xuất (nếu đã tạo)")
    private String stockIssueCode;

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

    public Instant getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAttachmentNote() {
        return attachmentNote;
    }

    public void setAttachmentNote(String attachmentNote) {
        this.attachmentNote = attachmentNote;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public AllocationRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AllocationRequestStatus status) {
        this.status = status;
    }

    public String getBeneficiaryNote() {
        return beneficiaryNote;
    }

    public void setBeneficiaryNote(String beneficiaryNote) {
        this.beneficiaryNote = beneficiaryNote;
    }

    public AssigneeType getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(AssigneeType assigneeType) {
        this.assigneeType = assigneeType;
    }

    public EmployeeDTO getBeneficiaryEmployee() {
        return beneficiaryEmployee;
    }

    public void setBeneficiaryEmployee(EmployeeDTO beneficiaryEmployee) {
        this.beneficiaryEmployee = beneficiaryEmployee;
    }

    public DepartmentDTO getBeneficiaryDepartment() {
        return beneficiaryDepartment;
    }

    public void setBeneficiaryDepartment(DepartmentDTO beneficiaryDepartment) {
        this.beneficiaryDepartment = beneficiaryDepartment;
    }

    public LocationDTO getBeneficiaryLocation() {
        return beneficiaryLocation;
    }

    public void setBeneficiaryLocation(LocationDTO beneficiaryLocation) {
        this.beneficiaryLocation = beneficiaryLocation;
    }

    public EmployeeDTO getRequester() {
        return requester;
    }

    public void setRequester(EmployeeDTO requester) {
        this.requester = requester;
    }

    public Long getStockIssueId() {
        return stockIssueId;
    }

    public void setStockIssueId(Long stockIssueId) {
        this.stockIssueId = stockIssueId;
    }

    public String getStockIssueCode() {
        return stockIssueCode;
    }

    public void setStockIssueCode(String stockIssueCode) {
        this.stockIssueCode = stockIssueCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AllocationRequestDTO)) {
            return false;
        }

        AllocationRequestDTO allocationRequestDTO = (AllocationRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, allocationRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AllocationRequestDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", reason='" + getReason() + "'" +
            ", attachmentNote='" + getAttachmentNote() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", beneficiaryNote='" + getBeneficiaryNote() + "'" +
            ", assigneeType=" + getAssigneeType() +
            ", beneficiaryEmployee=" + getBeneficiaryEmployee() +
            ", beneficiaryDepartment=" + getBeneficiaryDepartment() +
            ", beneficiaryLocation=" + getBeneficiaryLocation() +
            ", requester=" + getRequester() +
            "}";
    }
}

package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import com.gindevp.app.domain.enumeration.RepairResolution;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.RepairRequest} entity.
 */
@Schema(description = "Yêu cầu sửa chữa — có thể nhiều thiết bị (lines); equipment là thiết bị đầu tiên khi có dòng")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RepairRequestDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    private Instant requestDate;

    @Size(max = 100)
    private String problemCategory;

    @Size(max = 2000)
    private String description;

    @Size(max = 2000)
    private String attachmentNote;

    @NotNull
    private RepairRequestStatus status;

    @Size(max = 2000)
    @Schema(description = "Lý do từ chối (khi từ chối yêu cầu)")
    private String rejectionReason;

    @Size(max = 2000)
    private String resolutionNote;

    private RepairResolution repairOutcome;

    private EmployeeDTO requester;

    private EquipmentDTO equipment;

    private List<RepairRequestLineDTO> lines;

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

    public String getProblemCategory() {
        return problemCategory;
    }

    public void setProblemCategory(String problemCategory) {
        this.problemCategory = problemCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachmentNote() {
        return attachmentNote;
    }

    public void setAttachmentNote(String attachmentNote) {
        this.attachmentNote = attachmentNote;
    }

    public RepairRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RepairRequestStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }

    public RepairResolution getRepairOutcome() {
        return repairOutcome;
    }

    public void setRepairOutcome(RepairResolution repairOutcome) {
        this.repairOutcome = repairOutcome;
    }

    public EmployeeDTO getRequester() {
        return requester;
    }

    public void setRequester(EmployeeDTO requester) {
        this.requester = requester;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
    }

    public List<RepairRequestLineDTO> getLines() {
        return lines;
    }

    public void setLines(List<RepairRequestLineDTO> lines) {
        this.lines = lines;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepairRequestDTO)) {
            return false;
        }

        RepairRequestDTO repairRequestDTO = (RepairRequestDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, repairRequestDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RepairRequestDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", problemCategory='" + getProblemCategory() + "'" +
            ", description='" + getDescription() + "'" +
            ", attachmentNote='" + getAttachmentNote() + "'" +
            ", status='" + getStatus() + "'" +
            ", rejectionReason='" + getRejectionReason() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", requester=" + getRequester() +
            ", equipment=" + getEquipment() +
            "}";
    }
}

package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.RepairRequestStatus;
import com.gindevp.app.domain.enumeration.RepairResolution;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Yêu cầu sửa chữa — gắn 1 thiết bị (Phase 1 đơn giản)
 */
@Entity
@Table(name = "repair_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RepairRequest implements Serializable {

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
    @Column(name = "request_date", nullable = false)
    private Instant requestDate;

    @Size(max = 100)
    @Column(name = "problem_category", length = 100)
    private String problemCategory;

    @Size(max = 2000)
    @Column(name = "description", length = 2000)
    private String description;

    /** Link/ghi chú ảnh, tài liệu đính kèm (Phase 1: text) */
    @Size(max = 2000)
    @Column(name = "attachment_note", length = 2000)
    private String attachmentNote;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RepairRequestStatus status;

    @Size(max = 2000)
    @Column(name = "resolution_note", length = 2000)
    private String resolutionNote;

    @Enumerated(EnumType.STRING)
    @Column(name = "repair_outcome", length = 50)
    private RepairResolution repairOutcome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetItem", "supplier" }, allowSetters = true)
    private Equipment equipment;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RepairRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public RepairRequest code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getRequestDate() {
        return this.requestDate;
    }

    public RepairRequest requestDate(Instant requestDate) {
        this.setRequestDate(requestDate);
        return this;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public String getProblemCategory() {
        return this.problemCategory;
    }

    public RepairRequest problemCategory(String problemCategory) {
        this.setProblemCategory(problemCategory);
        return this;
    }

    public void setProblemCategory(String problemCategory) {
        this.problemCategory = problemCategory;
    }

    public String getDescription() {
        return this.description;
    }

    public RepairRequest description(String description) {
        this.setDescription(description);
        return this;
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

    public RepairRequest attachmentNote(String attachmentNote) {
        this.setAttachmentNote(attachmentNote);
        return this;
    }

    public RepairRequestStatus getStatus() {
        return this.status;
    }

    public RepairRequest status(RepairRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(RepairRequestStatus status) {
        this.status = status;
    }

    public String getResolutionNote() {
        return this.resolutionNote;
    }

    public RepairRequest resolutionNote(String resolutionNote) {
        this.setResolutionNote(resolutionNote);
        return this;
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

    public Employee getRequester() {
        return this.requester;
    }

    public void setRequester(Employee employee) {
        this.requester = employee;
    }

    public RepairRequest requester(Employee employee) {
        this.setRequester(employee);
        return this;
    }

    public Equipment getEquipment() {
        return this.equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public RepairRequest equipment(Equipment equipment) {
        this.setEquipment(equipment);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepairRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((RepairRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RepairRequest{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", problemCategory='" + getProblemCategory() + "'" +
            ", description='" + getDescription() + "'" +
            ", attachmentNote='" + getAttachmentNote() + "'" +
            ", status='" + getStatus() + "'" +
            ", resolutionNote='" + getResolutionNote() + "'" +
            ", repairOutcome='" + getRepairOutcome() + "'" +
            "}";
    }
}

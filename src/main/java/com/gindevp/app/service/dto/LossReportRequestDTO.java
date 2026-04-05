package com.gindevp.app.service.dto;

import com.gindevp.app.domain.enumeration.LossReportKind;
import com.gindevp.app.domain.enumeration.LossReportRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Schema(description = "Yêu cầu báo mất tài sản đang giữ")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LossReportRequestDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 20)
    private String code;

    @NotNull
    private Instant requestDate;

    @NotNull
    private LossReportRequestStatus status;

    @NotNull
    private LossReportKind lossKind;

    @Min(1)
    private Integer quantity;

    @Size(max = 500)
    private String lossOccurredAt;

    @Size(max = 1000)
    private String lossLocation;

    @Size(max = 2000)
    private String reason;

    @Size(max = 2000)
    private String lossDescription;

    private EmployeeDTO requester;

    private EquipmentDTO equipment;

    /** Chỉ id + mã item khi hiển thị list */
    private ConsumableAssignmentRefDTO consumableAssignment;

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

    public LossReportRequestStatus getStatus() {
        return status;
    }

    public void setStatus(LossReportRequestStatus status) {
        this.status = status;
    }

    public LossReportKind getLossKind() {
        return lossKind;
    }

    public void setLossKind(LossReportKind lossKind) {
        this.lossKind = lossKind;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getLossOccurredAt() {
        return lossOccurredAt;
    }

    public void setLossOccurredAt(String lossOccurredAt) {
        this.lossOccurredAt = lossOccurredAt;
    }

    public String getLossLocation() {
        return lossLocation;
    }

    public void setLossLocation(String lossLocation) {
        this.lossLocation = lossLocation;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLossDescription() {
        return lossDescription;
    }

    public void setLossDescription(String lossDescription) {
        this.lossDescription = lossDescription;
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

    public ConsumableAssignmentRefDTO getConsumableAssignment() {
        return consumableAssignment;
    }

    public void setConsumableAssignment(ConsumableAssignmentRefDTO consumableAssignment) {
        this.consumableAssignment = consumableAssignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LossReportRequestDTO)) return false;
        LossReportRequestDTO that = (LossReportRequestDTO) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

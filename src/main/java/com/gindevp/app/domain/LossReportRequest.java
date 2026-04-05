package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.LossReportKind;
import com.gindevp.app.domain.enumeration.LossReportRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Yêu cầu báo mất — NV tạo, QLTS/Admin/GĐ xác nhận → cập nhật trạng thái mất.
 */
@Entity
@Table(name = "loss_report_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LossReportRequest implements Serializable {

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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private LossReportRequestStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "loss_kind", nullable = false, length = 20)
    private LossReportKind lossKind;

    /** Vật tư: số lượng báo mất (thiết bị: null hoặc 1). */
    @Min(1)
    @Column(name = "quantity")
    private Integer quantity;

    @Size(max = 2000)
    @Column(name = "reason", length = 2000)
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetItem", "supplier" }, allowSetters = true)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private ConsumableAssignment consumableAssignment;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Employee getRequester() {
        return requester;
    }

    public void setRequester(Employee requester) {
        this.requester = requester;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public ConsumableAssignment getConsumableAssignment() {
        return consumableAssignment;
    }

    public void setConsumableAssignment(ConsumableAssignment consumableAssignment) {
        this.consumableAssignment = consumableAssignment;
    }
}

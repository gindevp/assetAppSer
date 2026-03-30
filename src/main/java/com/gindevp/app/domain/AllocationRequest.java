package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.AllocationRequestStatus;
import com.gindevp.app.domain.enumeration.AssigneeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Yêu cầu cấp phát
 */
@Entity
@Table(name = "allocation_request")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AllocationRequest implements Serializable {

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

    @Size(max = 2000)
    @Column(name = "reason", length = 2000)
    private String reason;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AllocationRequestStatus status;

    /**
     * Đối tượng nhận (người khác / phòng / vị trí) — bind theo assigneeType
     */
    @Size(max = 500)
    @Column(name = "beneficiary_note", length = 500)
    private String beneficiaryNote;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "assignee_type", nullable = false)
    private AssigneeType assigneeType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee beneficiaryEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department beneficiaryDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location beneficiaryLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee requester;

    @OneToOne(mappedBy = "allocationRequest", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "employee", "department", "location", "allocationRequest" }, allowSetters = true)
    private StockIssue stockIssue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AllocationRequest id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public AllocationRequest code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getRequestDate() {
        return this.requestDate;
    }

    public AllocationRequest requestDate(Instant requestDate) {
        this.setRequestDate(requestDate);
        return this;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public String getReason() {
        return this.reason;
    }

    public AllocationRequest reason(String reason) {
        this.setReason(reason);
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public AllocationRequestStatus getStatus() {
        return this.status;
    }

    public AllocationRequest status(AllocationRequestStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(AllocationRequestStatus status) {
        this.status = status;
    }

    public String getBeneficiaryNote() {
        return this.beneficiaryNote;
    }

    public AllocationRequest beneficiaryNote(String beneficiaryNote) {
        this.setBeneficiaryNote(beneficiaryNote);
        return this;
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

    public AllocationRequest assigneeType(AssigneeType assigneeType) {
        this.setAssigneeType(assigneeType);
        return this;
    }

    public Employee getBeneficiaryEmployee() {
        return beneficiaryEmployee;
    }

    public void setBeneficiaryEmployee(Employee beneficiaryEmployee) {
        this.beneficiaryEmployee = beneficiaryEmployee;
    }

    public AllocationRequest beneficiaryEmployee(Employee beneficiaryEmployee) {
        this.setBeneficiaryEmployee(beneficiaryEmployee);
        return this;
    }

    public Department getBeneficiaryDepartment() {
        return beneficiaryDepartment;
    }

    public void setBeneficiaryDepartment(Department beneficiaryDepartment) {
        this.beneficiaryDepartment = beneficiaryDepartment;
    }

    public AllocationRequest beneficiaryDepartment(Department beneficiaryDepartment) {
        this.setBeneficiaryDepartment(beneficiaryDepartment);
        return this;
    }

    public Location getBeneficiaryLocation() {
        return beneficiaryLocation;
    }

    public void setBeneficiaryLocation(Location beneficiaryLocation) {
        this.beneficiaryLocation = beneficiaryLocation;
    }

    public AllocationRequest beneficiaryLocation(Location beneficiaryLocation) {
        this.setBeneficiaryLocation(beneficiaryLocation);
        return this;
    }

    public Employee getRequester() {
        return this.requester;
    }

    public void setRequester(Employee employee) {
        this.requester = employee;
    }

    public AllocationRequest requester(Employee employee) {
        this.setRequester(employee);
        return this;
    }

    public StockIssue getStockIssue() {
        return stockIssue;
    }

    public void setStockIssue(StockIssue stockIssue) {
        this.stockIssue = stockIssue;
    }

    public AllocationRequest stockIssue(StockIssue stockIssue) {
        this.setStockIssue(stockIssue);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AllocationRequest)) {
            return false;
        }
        return getId() != null && getId().equals(((AllocationRequest) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AllocationRequest{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", reason='" + getReason() + "'" +
            ", status='" + getStatus() + "'" +
            ", beneficiaryNote='" + getBeneficiaryNote() + "'" +
            ", assigneeType='" + getAssigneeType() + "'" +
            "}";
    }
}

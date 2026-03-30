package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gindevp.app.domain.enumeration.AssigneeType;
import com.gindevp.app.domain.enumeration.DocumentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Phiếu xuất / cấp phát
 */
@Entity
@Table(name = "stock_issue")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class StockIssue implements Serializable {

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
    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "assignee_type", nullable = false)
    private AssigneeType assigneeType;

    @Size(max = 2000)
    @Column(name = "note", length = 2000)
    private String note;

    /**
     * nullable: chỉ khi assigneeType = EMPLOYEE
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    /**
     * Phiếu xuất sinh từ YC cấp phát (khi chuyển sang Đã tạo phiếu xuất).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "requester", "beneficiaryEmployee", "beneficiaryDepartment", "beneficiaryLocation" }, allowSetters = true)
    private AllocationRequest allocationRequest;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public StockIssue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public StockIssue code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getIssueDate() {
        return this.issueDate;
    }

    public StockIssue issueDate(LocalDate issueDate) {
        this.setIssueDate(issueDate);
        return this;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public DocumentStatus getStatus() {
        return this.status;
    }

    public StockIssue status(DocumentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(DocumentStatus status) {
        this.status = status;
    }

    public AssigneeType getAssigneeType() {
        return this.assigneeType;
    }

    public StockIssue assigneeType(AssigneeType assigneeType) {
        this.setAssigneeType(assigneeType);
        return this;
    }

    public void setAssigneeType(AssigneeType assigneeType) {
        this.assigneeType = assigneeType;
    }

    public String getNote() {
        return this.note;
    }

    public StockIssue note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public StockIssue employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public StockIssue department(Department department) {
        this.setDepartment(department);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public StockIssue location(Location location) {
        this.setLocation(location);
        return this;
    }

    public AllocationRequest getAllocationRequest() {
        return allocationRequest;
    }

    public void setAllocationRequest(AllocationRequest allocationRequest) {
        this.allocationRequest = allocationRequest;
    }

    public StockIssue allocationRequest(AllocationRequest allocationRequest) {
        this.setAllocationRequest(allocationRequest);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StockIssue)) {
            return false;
        }
        return getId() != null && getId().equals(((StockIssue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "StockIssue{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", issueDate='" + getIssueDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", assigneeType='" + getAssigneeType() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}

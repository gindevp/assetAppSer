package com.gindevp.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Gán vật tư theo số lượng
 */
@Entity
@Table(name = "consumable_assignment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConsumableAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "assigned_date", nullable = false)
    private LocalDate assignedDate;

    @Min(value = 0)
    @Column(name = "returned_quantity")
    private Integer returnedQuantity;

    @Size(max = 1000)
    @Column(name = "note", length = 1000)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "assetLine" }, allowSetters = true)
    private AssetItem assetItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "department" }, allowSetters = true)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ConsumableAssignment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public ConsumableAssignment quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getAssignedDate() {
        return this.assignedDate;
    }

    public ConsumableAssignment assignedDate(LocalDate assignedDate) {
        this.setAssignedDate(assignedDate);
        return this;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Integer getReturnedQuantity() {
        return this.returnedQuantity;
    }

    public ConsumableAssignment returnedQuantity(Integer returnedQuantity) {
        this.setReturnedQuantity(returnedQuantity);
        return this;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public String getNote() {
        return this.note;
    }

    public ConsumableAssignment note(String note) {
        this.setNote(note);
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AssetItem getAssetItem() {
        return this.assetItem;
    }

    public void setAssetItem(AssetItem assetItem) {
        this.assetItem = assetItem;
    }

    public ConsumableAssignment assetItem(AssetItem assetItem) {
        this.setAssetItem(assetItem);
        return this;
    }

    public Employee getEmployee() {
        return this.employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public ConsumableAssignment employee(Employee employee) {
        this.setEmployee(employee);
        return this;
    }

    public Department getDepartment() {
        return this.department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public ConsumableAssignment department(Department department) {
        this.setDepartment(department);
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ConsumableAssignment location(Location location) {
        this.setLocation(location);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsumableAssignment)) {
            return false;
        }
        return getId() != null && getId().equals(((ConsumableAssignment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConsumableAssignment{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", assignedDate='" + getAssignedDate() + "'" +
            ", returnedQuantity=" + getReturnedQuantity() +
            ", note='" + getNote() + "'" +
            "}";
    }
}

package com.gindevp.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.ConsumableAssignment} entity.
 */
@Schema(description = "Gán vật tư theo số lượng")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConsumableAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private LocalDate assignedDate;

    @Min(value = 0)
    private Integer returnedQuantity;

    @Size(max = 1000)
    private String note;

    private AssetItemDTO assetItem;

    private EmployeeDTO employee;

    private DepartmentDTO department;

    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public Integer getReturnedQuantity() {
        return returnedQuantity;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AssetItemDTO getAssetItem() {
        return assetItem;
    }

    public void setAssetItem(AssetItemDTO assetItem) {
        this.assetItem = assetItem;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsumableAssignmentDTO)) {
            return false;
        }

        ConsumableAssignmentDTO consumableAssignmentDTO = (ConsumableAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, consumableAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConsumableAssignmentDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", assignedDate='" + getAssignedDate() + "'" +
            ", returnedQuantity=" + getReturnedQuantity() +
            ", note='" + getNote() + "'" +
            ", assetItem=" + getAssetItem() +
            ", employee=" + getEmployee() +
            ", department=" + getDepartment() +
            ", location=" + getLocation() +
            "}";
    }
}

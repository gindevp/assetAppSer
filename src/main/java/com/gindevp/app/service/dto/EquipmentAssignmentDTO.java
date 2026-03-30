package com.gindevp.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.gindevp.app.domain.EquipmentAssignment} entity.
 */
@Schema(description = "Gán hiện tại: thiết bị đang ở ai/đâu")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EquipmentAssignmentDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate assignedDate;

    private LocalDate returnedDate;

    @Size(max = 1000)
    private String note;

    private EquipmentDTO equipment;

    private EmployeeDTO employee;

    private DepartmentDTO department;

    private LocationDTO location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public EquipmentDTO getEquipment() {
        return equipment;
    }

    public void setEquipment(EquipmentDTO equipment) {
        this.equipment = equipment;
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
        if (!(o instanceof EquipmentAssignmentDTO)) {
            return false;
        }

        EquipmentAssignmentDTO equipmentAssignmentDTO = (EquipmentAssignmentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, equipmentAssignmentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EquipmentAssignmentDTO{" +
            "id=" + getId() +
            ", assignedDate='" + getAssignedDate() + "'" +
            ", returnedDate='" + getReturnedDate() + "'" +
            ", note='" + getNote() + "'" +
            ", equipment=" + getEquipment() +
            ", employee=" + getEmployee() +
            ", department=" + getDepartment() +
            ", location=" + getLocation() +
            "}";
    }
}

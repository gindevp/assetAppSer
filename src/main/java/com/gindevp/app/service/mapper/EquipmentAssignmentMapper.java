package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.Department;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.EquipmentAssignment;
import com.gindevp.app.domain.Location;
import com.gindevp.app.service.dto.DepartmentDTO;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.EquipmentAssignmentDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EquipmentAssignment} and its DTO {@link EquipmentAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentAssignmentMapper extends EntityMapper<EquipmentAssignmentDTO, EquipmentAssignment> {
    @Mapping(target = "equipmentId", source = "equipment.id")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    /** Gồm phòng ban / vị trí của nhân viên — khi phiếu gán chỉ set employee, department trên assignment có thể null */
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeWithDeptLoc")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    EquipmentAssignmentDTO toDto(EquipmentAssignment s);

    @Override
    @BeanMapping(ignoreUnmappedSourceProperties = { "equipmentId" })
    EquipmentAssignment toEntity(EquipmentAssignmentDTO dto);

    @Named("equipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);

    @Named("employeeWithDeptLoc")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    EmployeeDTO toDtoEmployeeWithDeptLoc(Employee employee);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    LocationDTO toDtoLocationName(Location location);
}

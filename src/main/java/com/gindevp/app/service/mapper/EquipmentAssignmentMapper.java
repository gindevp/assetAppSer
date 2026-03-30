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
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeFullName")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    EquipmentAssignmentDTO toDto(EquipmentAssignment s);

    @Named("equipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);

    @Named("employeeFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    EmployeeDTO toDtoEmployeeFullName(Employee employee);

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

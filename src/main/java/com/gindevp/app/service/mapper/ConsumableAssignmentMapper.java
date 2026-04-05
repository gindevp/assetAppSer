package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.ConsumableAssignment;
import com.gindevp.app.domain.Department;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Location;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.ConsumableAssignmentDTO;
import com.gindevp.app.service.dto.DepartmentDTO;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ConsumableAssignment} and its DTO {@link ConsumableAssignmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConsumableAssignmentMapper extends EntityMapper<ConsumableAssignmentDTO, ConsumableAssignment> {
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeFullName")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    ConsumableAssignmentDTO toDto(ConsumableAssignment s);

    @Named("assetItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "unit", source = "unit")
    AssetItemDTO toDtoAssetItemName(AssetItem assetItem);

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

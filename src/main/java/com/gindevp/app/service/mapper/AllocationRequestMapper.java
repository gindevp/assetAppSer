package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.*;
import com.gindevp.app.service.dto.AllocationRequestDTO;
import com.gindevp.app.service.dto.DepartmentDTO;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AllocationRequest} and its DTO {@link AllocationRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface AllocationRequestMapper extends EntityMapper<AllocationRequestDTO, AllocationRequest> {
    @Override
    @Mapping(target = "stockIssue", ignore = true)
    @Mapping(target = "requester", source = "requester", qualifiedByName = "employeeRef")
    @Mapping(target = "beneficiaryEmployee", source = "beneficiaryEmployee", qualifiedByName = "employeeRef")
    @Mapping(target = "beneficiaryDepartment", source = "beneficiaryDepartment", qualifiedByName = "departmentRef")
    @Mapping(target = "beneficiaryLocation", source = "beneficiaryLocation", qualifiedByName = "locationRef")
    AllocationRequest toEntity(AllocationRequestDTO dto);

    @Override
    @Mapping(target = "requester", source = "requester", qualifiedByName = "employeeFullName")
    @Mapping(target = "beneficiaryEmployee", source = "beneficiaryEmployee", qualifiedByName = "beneficiaryEmployeeDto")
    @Mapping(target = "beneficiaryDepartment", source = "beneficiaryDepartment", qualifiedByName = "beneficiaryDepartmentDto")
    @Mapping(target = "beneficiaryLocation", source = "beneficiaryLocation", qualifiedByName = "beneficiaryLocationDto")
    @Mapping(target = "stockIssueId", source = "stockIssue.id")
    @Mapping(target = "stockIssueCode", source = "stockIssue.code")
    AllocationRequestDTO toDto(AllocationRequest s);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "stockIssue", ignore = true)
    @Mapping(target = "requester", source = "requester", qualifiedByName = "employeeRef")
    @Mapping(target = "beneficiaryEmployee", source = "beneficiaryEmployee", qualifiedByName = "employeeRef")
    @Mapping(target = "beneficiaryDepartment", source = "beneficiaryDepartment", qualifiedByName = "departmentRef")
    @Mapping(target = "beneficiaryLocation", source = "beneficiaryLocation", qualifiedByName = "locationRef")
    void partialUpdate(@MappingTarget AllocationRequest entity, AllocationRequestDTO dto);

    @Named("employeeFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "department", source = "department", qualifiedByName = "beneficiaryDepartmentDto")
    EmployeeDTO toDtoEmployeeFullName(Employee employee);

    @Named("beneficiaryEmployeeDto")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "code", source = "code")
    EmployeeDTO toBeneficiaryEmployeeDto(Employee employee);

    @Named("beneficiaryDepartmentDto")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toBeneficiaryDepartmentDto(Department department);

    @Named("beneficiaryLocationDto")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    LocationDTO toBeneficiaryLocationDto(Location location);

    @Named("employeeRef")
    default Employee employeeRef(EmployeeDTO dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }
        Employee e = new Employee();
        e.setId(dto.getId());
        return e;
    }

    @Named("departmentRef")
    default Department departmentRef(DepartmentDTO dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }
        Department d = new Department();
        d.setId(dto.getId());
        return d;
    }

    @Named("locationRef")
    default Location locationRef(LocationDTO dto) {
        if (dto == null || dto.getId() == null) {
            return null;
        }
        Location l = new Location();
        l.setId(dto.getId());
        return l;
    }
}

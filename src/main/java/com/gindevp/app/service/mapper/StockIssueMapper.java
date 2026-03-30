package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AllocationRequest;
import com.gindevp.app.domain.Department;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Location;
import com.gindevp.app.domain.StockIssue;
import com.gindevp.app.service.dto.DepartmentDTO;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.LocationDTO;
import com.gindevp.app.service.dto.StockIssueDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockIssue} and its DTO {@link StockIssueDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockIssueMapper extends EntityMapper<StockIssueDTO, StockIssue> {
    @Override
    @Mapping(target = "allocationRequest", source = "allocationRequestId", qualifiedByName = "allocationRequestRef")
    StockIssue toEntity(StockIssueDTO dto);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "allocationRequest", source = "allocationRequestId", qualifiedByName = "allocationRequestRef")
    void partialUpdate(@MappingTarget StockIssue entity, StockIssueDTO dto);

    @Mapping(target = "employee", source = "employee", qualifiedByName = "employeeFullName")
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    @Mapping(target = "allocationRequestId", source = "allocationRequest.id")
    StockIssueDTO toDto(StockIssue s);

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

    @Named("allocationRequestRef")
    default AllocationRequest allocationRequestRef(Long id) {
        if (id == null) {
            return null;
        }
        AllocationRequest ar = new AllocationRequest();
        ar.setId(id);
        return ar;
    }
}

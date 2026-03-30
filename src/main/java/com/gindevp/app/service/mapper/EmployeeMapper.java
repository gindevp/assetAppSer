package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.Department;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Location;
import com.gindevp.app.service.dto.DepartmentDTO;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.LocationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    @Mapping(target = "department", source = "department", qualifiedByName = "departmentName")
    @Mapping(target = "location", source = "location", qualifiedByName = "locationName")
    EmployeeDTO toDto(Employee s);

    @Named("departmentName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoDepartmentName(Department department);

    @Named("locationName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "code", source = "code")
    LocationDTO toDtoLocationName(Location location);
}

package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.ReturnRequest;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.ReturnRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnRequest} and its DTO {@link ReturnRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnRequestMapper extends EntityMapper<ReturnRequestDTO, ReturnRequest> {
    @Mapping(target = "requester", source = "requester", qualifiedByName = "employeeFullName")
    ReturnRequestDTO toDto(ReturnRequest s);

    @Named("employeeFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    EmployeeDTO toDtoEmployeeFullName(Employee employee);
}

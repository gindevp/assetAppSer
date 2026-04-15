package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.Department;
import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.service.dto.DepartmentDTO;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.RepairRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RepairRequest} and its DTO {@link RepairRequestDTO}.
 */
@Mapper(componentModel = "spring", uses = { RepairRequestLineMapper.class, LocationMapper.class })
public interface RepairRequestMapper extends EntityMapper<RepairRequestDTO, RepairRequest> {
    @Mapping(target = "requester", source = "requester", qualifiedByName = "employeeFullName")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    @Mapping(target = "reportedLocation", source = "reportedLocation")
    @Mapping(target = "lines", source = "lines")
    RepairRequestDTO toDto(RepairRequest s);

    @Override
    @Mapping(target = "lines", ignore = true)
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "equipment", source = "equipment")
    RepairRequest toEntity(RepairRequestDTO dto);

    @Override
    @Mapping(target = "lines", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget RepairRequest entity, RepairRequestDTO dto);

    @Named("employeeFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "department", source = "department", qualifiedByName = "requesterDepartmentBrief")
    EmployeeDTO toDtoEmployeeFullName(Employee employee);

    @Named("requesterDepartmentBrief")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    DepartmentDTO toDtoRequesterDepartmentBrief(Department department);

    @Named("equipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);
}

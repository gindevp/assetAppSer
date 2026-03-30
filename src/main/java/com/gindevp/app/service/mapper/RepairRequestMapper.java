package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.Employee;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.RepairRequest;
import com.gindevp.app.service.dto.EmployeeDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.RepairRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RepairRequest} and its DTO {@link RepairRequestDTO}.
 */
@Mapper(componentModel = "spring")
public interface RepairRequestMapper extends EntityMapper<RepairRequestDTO, RepairRequest> {
    @Mapping(target = "requester", source = "requester", qualifiedByName = "employeeFullName")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    RepairRequestDTO toDto(RepairRequest s);

    @Named("employeeFullName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    EmployeeDTO toDtoEmployeeFullName(Employee employee);

    @Named("equipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);
}

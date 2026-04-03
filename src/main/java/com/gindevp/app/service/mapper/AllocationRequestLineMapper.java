package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AllocationRequest;
import com.gindevp.app.domain.AllocationRequestLine;
import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.service.dto.AllocationRequestDTO;
import com.gindevp.app.service.dto.AllocationRequestLineDTO;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.AssetLineDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AllocationRequestLine} and its DTO {@link AllocationRequestLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface AllocationRequestLineMapper extends EntityMapper<AllocationRequestLineDTO, AllocationRequestLine> {
    @Mapping(target = "request", source = "request", qualifiedByName = "allocationRequestCode")
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    @Mapping(target = "assetLine", source = "assetLine", qualifiedByName = "assetLineName")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    AllocationRequestLineDTO toDto(AllocationRequestLine s);

    @Named("allocationRequestCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    AllocationRequestDTO toDtoAllocationRequestCode(AllocationRequest allocationRequest);

    @Named("assetItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetItemDTO toDtoAssetItemName(AssetItem assetItem);

    @Named("assetLineName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    AssetLineDTO toDtoAssetLineName(AssetLine assetLine);

    @Named("equipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);
}

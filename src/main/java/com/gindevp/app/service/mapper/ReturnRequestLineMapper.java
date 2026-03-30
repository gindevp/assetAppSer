package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.ReturnRequest;
import com.gindevp.app.domain.ReturnRequestLine;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.ReturnRequestDTO;
import com.gindevp.app.service.dto.ReturnRequestLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReturnRequestLine} and its DTO {@link ReturnRequestLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReturnRequestLineMapper extends EntityMapper<ReturnRequestLineDTO, ReturnRequestLine> {
    @Mapping(target = "request", source = "request", qualifiedByName = "returnRequestCode")
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    ReturnRequestLineDTO toDto(ReturnRequestLine s);

    @Named("returnRequestCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    ReturnRequestDTO toDtoReturnRequestCode(ReturnRequest returnRequest);

    @Named("assetItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetItemDTO toDtoAssetItemName(AssetItem assetItem);

    @Named("equipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);
}

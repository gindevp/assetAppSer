package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.RepairRequestLine;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.RepairRequestLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RepairRequestLine} and its DTO {@link RepairRequestLineDTO}.
 */
@Mapper(componentModel = "spring", uses = { EquipmentMapper.class, AssetItemMapper.class })
public interface RepairRequestLineMapper extends EntityMapper<RepairRequestLineDTO, RepairRequestLine> {
    @Mapping(target = "repairRequest", ignore = true)
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "lineEquipmentEquipmentCode")
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "lineAssetItemBrief")
    RepairRequestLineDTO toDto(RepairRequestLine s);

    @Named("lineEquipmentEquipmentCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "equipmentCode", source = "equipmentCode")
    EquipmentDTO toDtoEquipmentEquipmentCode(Equipment equipment);

    @Named("lineAssetItemBrief")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "name", source = "name")
    AssetItemDTO toDtoAssetItemBrief(AssetItem assetItem);

    @Override
    @Mapping(target = "repairRequest", ignore = true)
    RepairRequestLine toEntity(RepairRequestLineDTO dto);
}

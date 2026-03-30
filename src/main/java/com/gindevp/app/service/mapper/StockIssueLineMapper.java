package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.StockIssue;
import com.gindevp.app.domain.StockIssueLine;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.StockIssueDTO;
import com.gindevp.app.service.dto.StockIssueLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockIssueLine} and its DTO {@link StockIssueLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockIssueLineMapper extends EntityMapper<StockIssueLineDTO, StockIssueLine> {
    @Mapping(target = "issue", source = "issue", qualifiedByName = "stockIssueCode")
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    @Mapping(target = "equipment", source = "equipment", qualifiedByName = "equipmentEquipmentCode")
    StockIssueLineDTO toDto(StockIssueLine s);

    @Named("stockIssueCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    StockIssueDTO toDtoStockIssueCode(StockIssue stockIssue);

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

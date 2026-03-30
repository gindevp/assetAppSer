package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.ConsumableStock;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.ConsumableStockDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ConsumableStock} and its DTO {@link ConsumableStockDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConsumableStockMapper extends EntityMapper<ConsumableStockDTO, ConsumableStock> {
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    ConsumableStockDTO toDto(ConsumableStock s);

    @Named("assetItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetItemDTO toDtoAssetItemName(AssetItem assetItem);
}

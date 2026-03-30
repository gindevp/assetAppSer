package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.AssetLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssetItem} and its DTO {@link AssetItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetItemMapper extends EntityMapper<AssetItemDTO, AssetItem> {
    @Mapping(target = "assetLine", source = "assetLine", qualifiedByName = "assetLineName")
    AssetItemDTO toDto(AssetItem s);

    @Named("assetLineName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetLineDTO toDtoAssetLineName(AssetLine assetLine);
}

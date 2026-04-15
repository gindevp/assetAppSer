package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetGroup;
import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.service.dto.AssetGroupDTO;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.AssetLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssetItem} and its DTO {@link AssetItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetItemMapper extends EntityMapper<AssetItemDTO, AssetItem> {
    @Mapping(target = "assetLine", source = "assetLine", qualifiedByName = "assetLineForItem")
    AssetItemDTO toDto(AssetItem s);

    /** Gồm nhóm tài sản — FE cần assetLine.assetGroup.id để hiển thị nhóm (vd. Tồn kho). */
    @Named("assetLineForItem")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "assetGroup", source = "assetGroup", qualifiedByName = "assetGroupForItem")
    AssetLineDTO toDtoAssetLineForItem(AssetLine assetLine);

    @Named("assetGroupForItem")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetGroupDTO toDtoAssetGroupForItem(AssetGroup assetGroup);
}

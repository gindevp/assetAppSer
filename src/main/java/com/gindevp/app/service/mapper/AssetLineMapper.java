package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetGroup;
import com.gindevp.app.domain.AssetLine;
import com.gindevp.app.service.dto.AssetGroupDTO;
import com.gindevp.app.service.dto.AssetLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssetLine} and its DTO {@link AssetLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetLineMapper extends EntityMapper<AssetLineDTO, AssetLine> {
    @Mapping(target = "assetGroup", source = "assetGroup", qualifiedByName = "assetGroupName")
    AssetLineDTO toDto(AssetLine s);

    @Named("assetGroupName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetGroupDTO toDtoAssetGroupName(AssetGroup assetGroup);
}

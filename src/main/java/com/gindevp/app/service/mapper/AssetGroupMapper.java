package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetGroup;
import com.gindevp.app.domain.AssetType;
import com.gindevp.app.service.dto.AssetGroupDTO;
import com.gindevp.app.service.dto.AssetTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssetGroup} and its DTO {@link AssetGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetGroupMapper extends EntityMapper<AssetGroupDTO, AssetGroup> {
    @Mapping(target = "assetType", source = "assetType", qualifiedByName = "assetTypeName")
    AssetGroupDTO toDto(AssetGroup s);

    @Named("assetTypeName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetTypeDTO toDtoAssetTypeName(AssetType assetType);
}

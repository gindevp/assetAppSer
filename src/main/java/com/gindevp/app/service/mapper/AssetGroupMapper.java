package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetGroup;
import com.gindevp.app.service.dto.AssetGroupDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssetGroup} and its DTO {@link AssetGroupDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetGroupMapper extends EntityMapper<AssetGroupDTO, AssetGroup> {
    AssetGroupDTO toDto(AssetGroup s);
}

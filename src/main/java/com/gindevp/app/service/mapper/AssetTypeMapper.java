package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetType;
import com.gindevp.app.service.dto.AssetTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AssetType} and its DTO {@link AssetTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface AssetTypeMapper extends EntityMapper<AssetTypeDTO, AssetType> {}

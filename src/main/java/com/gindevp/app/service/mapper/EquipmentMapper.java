package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.Equipment;
import com.gindevp.app.domain.Supplier;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.EquipmentDTO;
import com.gindevp.app.service.dto.SupplierDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Equipment} and its DTO {@link EquipmentDTO}.
 */
@Mapper(componentModel = "spring")
public interface EquipmentMapper extends EntityMapper<EquipmentDTO, Equipment> {
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    @Mapping(target = "supplier", source = "supplier", qualifiedByName = "supplierName")
    EquipmentDTO toDto(Equipment s);

    @Named("assetItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetItemDTO toDtoAssetItemName(AssetItem assetItem);

    @Named("supplierName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SupplierDTO toDtoSupplierName(Supplier supplier);
}

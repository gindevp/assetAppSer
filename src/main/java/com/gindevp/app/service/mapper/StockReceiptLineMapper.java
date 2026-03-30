package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.AssetItem;
import com.gindevp.app.domain.StockReceipt;
import com.gindevp.app.domain.StockReceiptLine;
import com.gindevp.app.service.dto.AssetItemDTO;
import com.gindevp.app.service.dto.StockReceiptDTO;
import com.gindevp.app.service.dto.StockReceiptLineDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockReceiptLine} and its DTO {@link StockReceiptLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockReceiptLineMapper extends EntityMapper<StockReceiptLineDTO, StockReceiptLine> {
    @Mapping(target = "receipt", source = "receipt", qualifiedByName = "stockReceiptCode")
    @Mapping(target = "assetItem", source = "assetItem", qualifiedByName = "assetItemName")
    StockReceiptLineDTO toDto(StockReceiptLine s);

    @Named("stockReceiptCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    StockReceiptDTO toDtoStockReceiptCode(StockReceipt stockReceipt);

    @Named("assetItemName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AssetItemDTO toDtoAssetItemName(AssetItem assetItem);
}

package com.gindevp.app.service.mapper;

import com.gindevp.app.domain.StockReceipt;
import com.gindevp.app.service.dto.StockReceiptDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockReceipt} and its DTO {@link StockReceiptDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockReceiptMapper extends EntityMapper<StockReceiptDTO, StockReceipt> {}

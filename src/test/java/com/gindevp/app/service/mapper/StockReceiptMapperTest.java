package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.StockReceiptAsserts.*;
import static com.gindevp.app.domain.StockReceiptTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockReceiptMapperTest {

    private StockReceiptMapper stockReceiptMapper;

    @BeforeEach
    void setUp() {
        stockReceiptMapper = new StockReceiptMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockReceiptSample1();
        var actual = stockReceiptMapper.toEntity(stockReceiptMapper.toDto(expected));
        assertStockReceiptAllPropertiesEquals(expected, actual);
    }
}

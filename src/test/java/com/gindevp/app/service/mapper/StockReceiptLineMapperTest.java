package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.StockReceiptLineAsserts.*;
import static com.gindevp.app.domain.StockReceiptLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockReceiptLineMapperTest {

    private StockReceiptLineMapper stockReceiptLineMapper;

    @BeforeEach
    void setUp() {
        stockReceiptLineMapper = new StockReceiptLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockReceiptLineSample1();
        var actual = stockReceiptLineMapper.toEntity(stockReceiptLineMapper.toDto(expected));
        assertStockReceiptLineAllPropertiesEquals(expected, actual);
    }
}

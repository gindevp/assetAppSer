package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.StockIssueLineAsserts.*;
import static com.gindevp.app.domain.StockIssueLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockIssueLineMapperTest {

    private StockIssueLineMapper stockIssueLineMapper;

    @BeforeEach
    void setUp() {
        stockIssueLineMapper = new StockIssueLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockIssueLineSample1();
        var actual = stockIssueLineMapper.toEntity(stockIssueLineMapper.toDto(expected));
        assertStockIssueLineAllPropertiesEquals(expected, actual);
    }
}

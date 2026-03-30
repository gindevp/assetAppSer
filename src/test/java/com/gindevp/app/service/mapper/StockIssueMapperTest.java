package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.StockIssueAsserts.*;
import static com.gindevp.app.domain.StockIssueTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StockIssueMapperTest {

    private StockIssueMapper stockIssueMapper;

    @BeforeEach
    void setUp() {
        stockIssueMapper = new StockIssueMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getStockIssueSample1();
        var actual = stockIssueMapper.toEntity(stockIssueMapper.toDto(expected));
        assertStockIssueAllPropertiesEquals(expected, actual);
    }
}

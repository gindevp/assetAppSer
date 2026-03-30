package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.ConsumableStockAsserts.*;
import static com.gindevp.app.domain.ConsumableStockTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsumableStockMapperTest {

    private ConsumableStockMapper consumableStockMapper;

    @BeforeEach
    void setUp() {
        consumableStockMapper = new ConsumableStockMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getConsumableStockSample1();
        var actual = consumableStockMapper.toEntity(consumableStockMapper.toDto(expected));
        assertConsumableStockAllPropertiesEquals(expected, actual);
    }
}

package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.ConsumableAssignmentAsserts.*;
import static com.gindevp.app.domain.ConsumableAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConsumableAssignmentMapperTest {

    private ConsumableAssignmentMapper consumableAssignmentMapper;

    @BeforeEach
    void setUp() {
        consumableAssignmentMapper = new ConsumableAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getConsumableAssignmentSample1();
        var actual = consumableAssignmentMapper.toEntity(consumableAssignmentMapper.toDto(expected));
        assertConsumableAssignmentAllPropertiesEquals(expected, actual);
    }
}

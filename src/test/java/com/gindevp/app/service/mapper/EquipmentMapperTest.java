package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.EquipmentAsserts.*;
import static com.gindevp.app.domain.EquipmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipmentMapperTest {

    private EquipmentMapper equipmentMapper;

    @BeforeEach
    void setUp() {
        equipmentMapper = new EquipmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEquipmentSample1();
        var actual = equipmentMapper.toEntity(equipmentMapper.toDto(expected));
        assertEquipmentAllPropertiesEquals(expected, actual);
    }
}

package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.EquipmentAssignmentAsserts.*;
import static com.gindevp.app.domain.EquipmentAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipmentAssignmentMapperTest {

    private EquipmentAssignmentMapper equipmentAssignmentMapper;

    @BeforeEach
    void setUp() {
        equipmentAssignmentMapper = new EquipmentAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEquipmentAssignmentSample1();
        var actual = equipmentAssignmentMapper.toEntity(equipmentAssignmentMapper.toDto(expected));
        assertEquipmentAssignmentAllPropertiesEquals(expected, actual);
    }
}

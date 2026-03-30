package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.RepairRequestAsserts.*;
import static com.gindevp.app.domain.RepairRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepairRequestMapperTest {

    private RepairRequestMapper repairRequestMapper;

    @BeforeEach
    void setUp() {
        repairRequestMapper = new RepairRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRepairRequestSample1();
        var actual = repairRequestMapper.toEntity(repairRequestMapper.toDto(expected));
        assertRepairRequestAllPropertiesEquals(expected, actual);
    }
}

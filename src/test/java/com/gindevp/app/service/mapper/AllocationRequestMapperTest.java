package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.AllocationRequestAsserts.*;
import static com.gindevp.app.domain.AllocationRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllocationRequestMapperTest {

    private AllocationRequestMapper allocationRequestMapper;

    @BeforeEach
    void setUp() {
        allocationRequestMapper = new AllocationRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAllocationRequestSample1();
        var actual = allocationRequestMapper.toEntity(allocationRequestMapper.toDto(expected));
        assertAllocationRequestAllPropertiesEquals(expected, actual);
    }
}

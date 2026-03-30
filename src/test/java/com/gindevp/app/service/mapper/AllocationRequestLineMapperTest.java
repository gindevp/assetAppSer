package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.AllocationRequestLineAsserts.*;
import static com.gindevp.app.domain.AllocationRequestLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllocationRequestLineMapperTest {

    private AllocationRequestLineMapper allocationRequestLineMapper;

    @BeforeEach
    void setUp() {
        allocationRequestLineMapper = new AllocationRequestLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAllocationRequestLineSample1();
        var actual = allocationRequestLineMapper.toEntity(allocationRequestLineMapper.toDto(expected));
        assertAllocationRequestLineAllPropertiesEquals(expected, actual);
    }
}

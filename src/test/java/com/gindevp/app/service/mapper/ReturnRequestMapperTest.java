package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.ReturnRequestAsserts.*;
import static com.gindevp.app.domain.ReturnRequestTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnRequestMapperTest {

    private ReturnRequestMapper returnRequestMapper;

    @BeforeEach
    void setUp() {
        returnRequestMapper = new ReturnRequestMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnRequestSample1();
        var actual = returnRequestMapper.toEntity(returnRequestMapper.toDto(expected));
        assertReturnRequestAllPropertiesEquals(expected, actual);
    }
}

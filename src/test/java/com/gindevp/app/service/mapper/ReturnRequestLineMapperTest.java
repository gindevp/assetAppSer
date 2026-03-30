package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.ReturnRequestLineAsserts.*;
import static com.gindevp.app.domain.ReturnRequestLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReturnRequestLineMapperTest {

    private ReturnRequestLineMapper returnRequestLineMapper;

    @BeforeEach
    void setUp() {
        returnRequestLineMapper = new ReturnRequestLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getReturnRequestLineSample1();
        var actual = returnRequestLineMapper.toEntity(returnRequestLineMapper.toDto(expected));
        assertReturnRequestLineAllPropertiesEquals(expected, actual);
    }
}

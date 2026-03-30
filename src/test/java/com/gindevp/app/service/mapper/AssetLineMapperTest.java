package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.AssetLineAsserts.*;
import static com.gindevp.app.domain.AssetLineTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssetLineMapperTest {

    private AssetLineMapper assetLineMapper;

    @BeforeEach
    void setUp() {
        assetLineMapper = new AssetLineMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssetLineSample1();
        var actual = assetLineMapper.toEntity(assetLineMapper.toDto(expected));
        assertAssetLineAllPropertiesEquals(expected, actual);
    }
}

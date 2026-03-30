package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.AssetTypeAsserts.*;
import static com.gindevp.app.domain.AssetTypeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssetTypeMapperTest {

    private AssetTypeMapper assetTypeMapper;

    @BeforeEach
    void setUp() {
        assetTypeMapper = new AssetTypeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssetTypeSample1();
        var actual = assetTypeMapper.toEntity(assetTypeMapper.toDto(expected));
        assertAssetTypeAllPropertiesEquals(expected, actual);
    }
}

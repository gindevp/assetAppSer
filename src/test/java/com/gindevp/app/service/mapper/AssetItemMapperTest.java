package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.AssetItemAsserts.*;
import static com.gindevp.app.domain.AssetItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssetItemMapperTest {

    private AssetItemMapper assetItemMapper;

    @BeforeEach
    void setUp() {
        assetItemMapper = new AssetItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssetItemSample1();
        var actual = assetItemMapper.toEntity(assetItemMapper.toDto(expected));
        assertAssetItemAllPropertiesEquals(expected, actual);
    }
}

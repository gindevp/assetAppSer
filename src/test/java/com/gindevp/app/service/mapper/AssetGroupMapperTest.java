package com.gindevp.app.service.mapper;

import static com.gindevp.app.domain.AssetGroupAsserts.*;
import static com.gindevp.app.domain.AssetGroupTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AssetGroupMapperTest {

    private AssetGroupMapper assetGroupMapper;

    @BeforeEach
    void setUp() {
        assetGroupMapper = new AssetGroupMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAssetGroupSample1();
        var actual = assetGroupMapper.toEntity(assetGroupMapper.toDto(expected));
        assertAssetGroupAllPropertiesEquals(expected, actual);
    }
}

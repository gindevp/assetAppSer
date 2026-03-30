package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetGroupTestSamples.*;
import static com.gindevp.app.domain.AssetLineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetLine.class);
        AssetLine assetLine1 = getAssetLineSample1();
        AssetLine assetLine2 = new AssetLine();
        assertThat(assetLine1).isNotEqualTo(assetLine2);

        assetLine2.setId(assetLine1.getId());
        assertThat(assetLine1).isEqualTo(assetLine2);

        assetLine2 = getAssetLineSample2();
        assertThat(assetLine1).isNotEqualTo(assetLine2);
    }

    @Test
    void assetGroupTest() {
        AssetLine assetLine = getAssetLineRandomSampleGenerator();
        AssetGroup assetGroupBack = getAssetGroupRandomSampleGenerator();

        assetLine.setAssetGroup(assetGroupBack);
        assertThat(assetLine.getAssetGroup()).isEqualTo(assetGroupBack);

        assetLine.assetGroup(null);
        assertThat(assetLine.getAssetGroup()).isNull();
    }
}

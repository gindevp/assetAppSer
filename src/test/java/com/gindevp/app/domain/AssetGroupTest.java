package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetGroupTestSamples.*;
import static com.gindevp.app.domain.AssetTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetGroupTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetGroup.class);
        AssetGroup assetGroup1 = getAssetGroupSample1();
        AssetGroup assetGroup2 = new AssetGroup();
        assertThat(assetGroup1).isNotEqualTo(assetGroup2);

        assetGroup2.setId(assetGroup1.getId());
        assertThat(assetGroup1).isEqualTo(assetGroup2);

        assetGroup2 = getAssetGroupSample2();
        assertThat(assetGroup1).isNotEqualTo(assetGroup2);
    }

    @Test
    void assetTypeTest() {
        AssetGroup assetGroup = getAssetGroupRandomSampleGenerator();
        AssetType assetTypeBack = getAssetTypeRandomSampleGenerator();

        assetGroup.setAssetType(assetTypeBack);
        assertThat(assetGroup.getAssetType()).isEqualTo(assetTypeBack);

        assetGroup.assetType(null);
        assertThat(assetGroup.getAssetType()).isNull();
    }
}

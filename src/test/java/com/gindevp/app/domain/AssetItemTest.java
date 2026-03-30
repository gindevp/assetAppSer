package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.AssetLineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetItem.class);
        AssetItem assetItem1 = getAssetItemSample1();
        AssetItem assetItem2 = new AssetItem();
        assertThat(assetItem1).isNotEqualTo(assetItem2);

        assetItem2.setId(assetItem1.getId());
        assertThat(assetItem1).isEqualTo(assetItem2);

        assetItem2 = getAssetItemSample2();
        assertThat(assetItem1).isNotEqualTo(assetItem2);
    }

    @Test
    void assetLineTest() {
        AssetItem assetItem = getAssetItemRandomSampleGenerator();
        AssetLine assetLineBack = getAssetLineRandomSampleGenerator();

        assetItem.setAssetLine(assetLineBack);
        assertThat(assetItem.getAssetLine()).isEqualTo(assetLineBack);

        assetItem.assetLine(null);
        assertThat(assetItem.getAssetLine()).isNull();
    }
}

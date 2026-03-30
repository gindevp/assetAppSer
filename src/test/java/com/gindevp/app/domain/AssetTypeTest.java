package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetTypeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetType.class);
        AssetType assetType1 = getAssetTypeSample1();
        AssetType assetType2 = new AssetType();
        assertThat(assetType1).isNotEqualTo(assetType2);

        assetType2.setId(assetType1.getId());
        assertThat(assetType1).isEqualTo(assetType2);

        assetType2 = getAssetTypeSample2();
        assertThat(assetType1).isNotEqualTo(assetType2);
    }
}

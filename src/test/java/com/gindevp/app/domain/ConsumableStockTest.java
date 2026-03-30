package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.ConsumableStockTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConsumableStockTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConsumableStock.class);
        ConsumableStock consumableStock1 = getConsumableStockSample1();
        ConsumableStock consumableStock2 = new ConsumableStock();
        assertThat(consumableStock1).isNotEqualTo(consumableStock2);

        consumableStock2.setId(consumableStock1.getId());
        assertThat(consumableStock1).isEqualTo(consumableStock2);

        consumableStock2 = getConsumableStockSample2();
        assertThat(consumableStock1).isNotEqualTo(consumableStock2);
    }

    @Test
    void assetItemTest() {
        ConsumableStock consumableStock = getConsumableStockRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        consumableStock.setAssetItem(assetItemBack);
        assertThat(consumableStock.getAssetItem()).isEqualTo(assetItemBack);

        consumableStock.assetItem(null);
        assertThat(consumableStock.getAssetItem()).isNull();
    }
}

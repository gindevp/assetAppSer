package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.StockReceiptLineTestSamples.*;
import static com.gindevp.app.domain.StockReceiptTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockReceiptLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockReceiptLine.class);
        StockReceiptLine stockReceiptLine1 = getStockReceiptLineSample1();
        StockReceiptLine stockReceiptLine2 = new StockReceiptLine();
        assertThat(stockReceiptLine1).isNotEqualTo(stockReceiptLine2);

        stockReceiptLine2.setId(stockReceiptLine1.getId());
        assertThat(stockReceiptLine1).isEqualTo(stockReceiptLine2);

        stockReceiptLine2 = getStockReceiptLineSample2();
        assertThat(stockReceiptLine1).isNotEqualTo(stockReceiptLine2);
    }

    @Test
    void receiptTest() {
        StockReceiptLine stockReceiptLine = getStockReceiptLineRandomSampleGenerator();
        StockReceipt stockReceiptBack = getStockReceiptRandomSampleGenerator();

        stockReceiptLine.setReceipt(stockReceiptBack);
        assertThat(stockReceiptLine.getReceipt()).isEqualTo(stockReceiptBack);

        stockReceiptLine.receipt(null);
        assertThat(stockReceiptLine.getReceipt()).isNull();
    }

    @Test
    void assetItemTest() {
        StockReceiptLine stockReceiptLine = getStockReceiptLineRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        stockReceiptLine.setAssetItem(assetItemBack);
        assertThat(stockReceiptLine.getAssetItem()).isEqualTo(assetItemBack);

        stockReceiptLine.assetItem(null);
        assertThat(stockReceiptLine.getAssetItem()).isNull();
    }
}

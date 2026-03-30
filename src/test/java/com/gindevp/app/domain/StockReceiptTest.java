package com.gindevp.app.domain;

import static com.gindevp.app.domain.StockReceiptTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockReceiptTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockReceipt.class);
        StockReceipt stockReceipt1 = getStockReceiptSample1();
        StockReceipt stockReceipt2 = new StockReceipt();
        assertThat(stockReceipt1).isNotEqualTo(stockReceipt2);

        stockReceipt2.setId(stockReceipt1.getId());
        assertThat(stockReceipt1).isEqualTo(stockReceipt2);

        stockReceipt2 = getStockReceiptSample2();
        assertThat(stockReceipt1).isNotEqualTo(stockReceipt2);
    }
}

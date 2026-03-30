package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockReceiptDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockReceiptDTO.class);
        StockReceiptDTO stockReceiptDTO1 = new StockReceiptDTO();
        stockReceiptDTO1.setId(1L);
        StockReceiptDTO stockReceiptDTO2 = new StockReceiptDTO();
        assertThat(stockReceiptDTO1).isNotEqualTo(stockReceiptDTO2);
        stockReceiptDTO2.setId(stockReceiptDTO1.getId());
        assertThat(stockReceiptDTO1).isEqualTo(stockReceiptDTO2);
        stockReceiptDTO2.setId(2L);
        assertThat(stockReceiptDTO1).isNotEqualTo(stockReceiptDTO2);
        stockReceiptDTO1.setId(null);
        assertThat(stockReceiptDTO1).isNotEqualTo(stockReceiptDTO2);
    }
}

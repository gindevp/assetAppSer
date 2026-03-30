package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockReceiptLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockReceiptLineDTO.class);
        StockReceiptLineDTO stockReceiptLineDTO1 = new StockReceiptLineDTO();
        stockReceiptLineDTO1.setId(1L);
        StockReceiptLineDTO stockReceiptLineDTO2 = new StockReceiptLineDTO();
        assertThat(stockReceiptLineDTO1).isNotEqualTo(stockReceiptLineDTO2);
        stockReceiptLineDTO2.setId(stockReceiptLineDTO1.getId());
        assertThat(stockReceiptLineDTO1).isEqualTo(stockReceiptLineDTO2);
        stockReceiptLineDTO2.setId(2L);
        assertThat(stockReceiptLineDTO1).isNotEqualTo(stockReceiptLineDTO2);
        stockReceiptLineDTO1.setId(null);
        assertThat(stockReceiptLineDTO1).isNotEqualTo(stockReceiptLineDTO2);
    }
}

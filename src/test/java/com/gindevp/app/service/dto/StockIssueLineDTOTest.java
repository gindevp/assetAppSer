package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockIssueLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockIssueLineDTO.class);
        StockIssueLineDTO stockIssueLineDTO1 = new StockIssueLineDTO();
        stockIssueLineDTO1.setId(1L);
        StockIssueLineDTO stockIssueLineDTO2 = new StockIssueLineDTO();
        assertThat(stockIssueLineDTO1).isNotEqualTo(stockIssueLineDTO2);
        stockIssueLineDTO2.setId(stockIssueLineDTO1.getId());
        assertThat(stockIssueLineDTO1).isEqualTo(stockIssueLineDTO2);
        stockIssueLineDTO2.setId(2L);
        assertThat(stockIssueLineDTO1).isNotEqualTo(stockIssueLineDTO2);
        stockIssueLineDTO1.setId(null);
        assertThat(stockIssueLineDTO1).isNotEqualTo(stockIssueLineDTO2);
    }
}

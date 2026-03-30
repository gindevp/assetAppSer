package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockIssueDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockIssueDTO.class);
        StockIssueDTO stockIssueDTO1 = new StockIssueDTO();
        stockIssueDTO1.setId(1L);
        StockIssueDTO stockIssueDTO2 = new StockIssueDTO();
        assertThat(stockIssueDTO1).isNotEqualTo(stockIssueDTO2);
        stockIssueDTO2.setId(stockIssueDTO1.getId());
        assertThat(stockIssueDTO1).isEqualTo(stockIssueDTO2);
        stockIssueDTO2.setId(2L);
        assertThat(stockIssueDTO1).isNotEqualTo(stockIssueDTO2);
        stockIssueDTO1.setId(null);
        assertThat(stockIssueDTO1).isNotEqualTo(stockIssueDTO2);
    }
}

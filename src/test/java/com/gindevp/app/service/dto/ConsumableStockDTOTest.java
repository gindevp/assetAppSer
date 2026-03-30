package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConsumableStockDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConsumableStockDTO.class);
        ConsumableStockDTO consumableStockDTO1 = new ConsumableStockDTO();
        consumableStockDTO1.setId(1L);
        ConsumableStockDTO consumableStockDTO2 = new ConsumableStockDTO();
        assertThat(consumableStockDTO1).isNotEqualTo(consumableStockDTO2);
        consumableStockDTO2.setId(consumableStockDTO1.getId());
        assertThat(consumableStockDTO1).isEqualTo(consumableStockDTO2);
        consumableStockDTO2.setId(2L);
        assertThat(consumableStockDTO1).isNotEqualTo(consumableStockDTO2);
        consumableStockDTO1.setId(null);
        assertThat(consumableStockDTO1).isNotEqualTo(consumableStockDTO2);
    }
}

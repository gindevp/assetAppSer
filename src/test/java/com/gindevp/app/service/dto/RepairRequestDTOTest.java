package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RepairRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RepairRequestDTO.class);
        RepairRequestDTO repairRequestDTO1 = new RepairRequestDTO();
        repairRequestDTO1.setId(1L);
        RepairRequestDTO repairRequestDTO2 = new RepairRequestDTO();
        assertThat(repairRequestDTO1).isNotEqualTo(repairRequestDTO2);
        repairRequestDTO2.setId(repairRequestDTO1.getId());
        assertThat(repairRequestDTO1).isEqualTo(repairRequestDTO2);
        repairRequestDTO2.setId(2L);
        assertThat(repairRequestDTO1).isNotEqualTo(repairRequestDTO2);
        repairRequestDTO1.setId(null);
        assertThat(repairRequestDTO1).isNotEqualTo(repairRequestDTO2);
    }
}

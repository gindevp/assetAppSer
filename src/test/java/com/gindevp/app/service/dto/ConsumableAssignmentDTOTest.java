package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConsumableAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConsumableAssignmentDTO.class);
        ConsumableAssignmentDTO consumableAssignmentDTO1 = new ConsumableAssignmentDTO();
        consumableAssignmentDTO1.setId(1L);
        ConsumableAssignmentDTO consumableAssignmentDTO2 = new ConsumableAssignmentDTO();
        assertThat(consumableAssignmentDTO1).isNotEqualTo(consumableAssignmentDTO2);
        consumableAssignmentDTO2.setId(consumableAssignmentDTO1.getId());
        assertThat(consumableAssignmentDTO1).isEqualTo(consumableAssignmentDTO2);
        consumableAssignmentDTO2.setId(2L);
        assertThat(consumableAssignmentDTO1).isNotEqualTo(consumableAssignmentDTO2);
        consumableAssignmentDTO1.setId(null);
        assertThat(consumableAssignmentDTO1).isNotEqualTo(consumableAssignmentDTO2);
    }
}

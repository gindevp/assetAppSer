package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EquipmentAssignmentDTO.class);
        EquipmentAssignmentDTO equipmentAssignmentDTO1 = new EquipmentAssignmentDTO();
        equipmentAssignmentDTO1.setId(1L);
        EquipmentAssignmentDTO equipmentAssignmentDTO2 = new EquipmentAssignmentDTO();
        assertThat(equipmentAssignmentDTO1).isNotEqualTo(equipmentAssignmentDTO2);
        equipmentAssignmentDTO2.setId(equipmentAssignmentDTO1.getId());
        assertThat(equipmentAssignmentDTO1).isEqualTo(equipmentAssignmentDTO2);
        equipmentAssignmentDTO2.setId(2L);
        assertThat(equipmentAssignmentDTO1).isNotEqualTo(equipmentAssignmentDTO2);
        equipmentAssignmentDTO1.setId(null);
        assertThat(equipmentAssignmentDTO1).isNotEqualTo(equipmentAssignmentDTO2);
    }
}

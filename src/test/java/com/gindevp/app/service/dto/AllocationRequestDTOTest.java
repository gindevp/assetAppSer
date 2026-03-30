package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AllocationRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AllocationRequestDTO.class);
        AllocationRequestDTO allocationRequestDTO1 = new AllocationRequestDTO();
        allocationRequestDTO1.setId(1L);
        AllocationRequestDTO allocationRequestDTO2 = new AllocationRequestDTO();
        assertThat(allocationRequestDTO1).isNotEqualTo(allocationRequestDTO2);
        allocationRequestDTO2.setId(allocationRequestDTO1.getId());
        assertThat(allocationRequestDTO1).isEqualTo(allocationRequestDTO2);
        allocationRequestDTO2.setId(2L);
        assertThat(allocationRequestDTO1).isNotEqualTo(allocationRequestDTO2);
        allocationRequestDTO1.setId(null);
        assertThat(allocationRequestDTO1).isNotEqualTo(allocationRequestDTO2);
    }
}

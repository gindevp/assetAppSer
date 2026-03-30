package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AllocationRequestLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AllocationRequestLineDTO.class);
        AllocationRequestLineDTO allocationRequestLineDTO1 = new AllocationRequestLineDTO();
        allocationRequestLineDTO1.setId(1L);
        AllocationRequestLineDTO allocationRequestLineDTO2 = new AllocationRequestLineDTO();
        assertThat(allocationRequestLineDTO1).isNotEqualTo(allocationRequestLineDTO2);
        allocationRequestLineDTO2.setId(allocationRequestLineDTO1.getId());
        assertThat(allocationRequestLineDTO1).isEqualTo(allocationRequestLineDTO2);
        allocationRequestLineDTO2.setId(2L);
        assertThat(allocationRequestLineDTO1).isNotEqualTo(allocationRequestLineDTO2);
        allocationRequestLineDTO1.setId(null);
        assertThat(allocationRequestLineDTO1).isNotEqualTo(allocationRequestLineDTO2);
    }
}

package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnRequestDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnRequestDTO.class);
        ReturnRequestDTO returnRequestDTO1 = new ReturnRequestDTO();
        returnRequestDTO1.setId(1L);
        ReturnRequestDTO returnRequestDTO2 = new ReturnRequestDTO();
        assertThat(returnRequestDTO1).isNotEqualTo(returnRequestDTO2);
        returnRequestDTO2.setId(returnRequestDTO1.getId());
        assertThat(returnRequestDTO1).isEqualTo(returnRequestDTO2);
        returnRequestDTO2.setId(2L);
        assertThat(returnRequestDTO1).isNotEqualTo(returnRequestDTO2);
        returnRequestDTO1.setId(null);
        assertThat(returnRequestDTO1).isNotEqualTo(returnRequestDTO2);
    }
}

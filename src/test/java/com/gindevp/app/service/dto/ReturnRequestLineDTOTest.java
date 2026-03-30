package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnRequestLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnRequestLineDTO.class);
        ReturnRequestLineDTO returnRequestLineDTO1 = new ReturnRequestLineDTO();
        returnRequestLineDTO1.setId(1L);
        ReturnRequestLineDTO returnRequestLineDTO2 = new ReturnRequestLineDTO();
        assertThat(returnRequestLineDTO1).isNotEqualTo(returnRequestLineDTO2);
        returnRequestLineDTO2.setId(returnRequestLineDTO1.getId());
        assertThat(returnRequestLineDTO1).isEqualTo(returnRequestLineDTO2);
        returnRequestLineDTO2.setId(2L);
        assertThat(returnRequestLineDTO1).isNotEqualTo(returnRequestLineDTO2);
        returnRequestLineDTO1.setId(null);
        assertThat(returnRequestLineDTO1).isNotEqualTo(returnRequestLineDTO2);
    }
}

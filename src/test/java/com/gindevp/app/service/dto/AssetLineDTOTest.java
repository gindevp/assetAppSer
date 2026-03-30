package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetLineDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetLineDTO.class);
        AssetLineDTO assetLineDTO1 = new AssetLineDTO();
        assetLineDTO1.setId(1L);
        AssetLineDTO assetLineDTO2 = new AssetLineDTO();
        assertThat(assetLineDTO1).isNotEqualTo(assetLineDTO2);
        assetLineDTO2.setId(assetLineDTO1.getId());
        assertThat(assetLineDTO1).isEqualTo(assetLineDTO2);
        assetLineDTO2.setId(2L);
        assertThat(assetLineDTO1).isNotEqualTo(assetLineDTO2);
        assetLineDTO1.setId(null);
        assertThat(assetLineDTO1).isNotEqualTo(assetLineDTO2);
    }
}

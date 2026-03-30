package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetTypeDTO.class);
        AssetTypeDTO assetTypeDTO1 = new AssetTypeDTO();
        assetTypeDTO1.setId(1L);
        AssetTypeDTO assetTypeDTO2 = new AssetTypeDTO();
        assertThat(assetTypeDTO1).isNotEqualTo(assetTypeDTO2);
        assetTypeDTO2.setId(assetTypeDTO1.getId());
        assertThat(assetTypeDTO1).isEqualTo(assetTypeDTO2);
        assetTypeDTO2.setId(2L);
        assertThat(assetTypeDTO1).isNotEqualTo(assetTypeDTO2);
        assetTypeDTO1.setId(null);
        assertThat(assetTypeDTO1).isNotEqualTo(assetTypeDTO2);
    }
}

package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetGroupDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetGroupDTO.class);
        AssetGroupDTO assetGroupDTO1 = new AssetGroupDTO();
        assetGroupDTO1.setId(1L);
        AssetGroupDTO assetGroupDTO2 = new AssetGroupDTO();
        assertThat(assetGroupDTO1).isNotEqualTo(assetGroupDTO2);
        assetGroupDTO2.setId(assetGroupDTO1.getId());
        assertThat(assetGroupDTO1).isEqualTo(assetGroupDTO2);
        assetGroupDTO2.setId(2L);
        assertThat(assetGroupDTO1).isNotEqualTo(assetGroupDTO2);
        assetGroupDTO1.setId(null);
        assertThat(assetGroupDTO1).isNotEqualTo(assetGroupDTO2);
    }
}

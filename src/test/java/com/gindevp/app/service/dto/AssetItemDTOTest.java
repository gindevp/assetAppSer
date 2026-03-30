package com.gindevp.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssetItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AssetItemDTO.class);
        AssetItemDTO assetItemDTO1 = new AssetItemDTO();
        assetItemDTO1.setId(1L);
        AssetItemDTO assetItemDTO2 = new AssetItemDTO();
        assertThat(assetItemDTO1).isNotEqualTo(assetItemDTO2);
        assetItemDTO2.setId(assetItemDTO1.getId());
        assertThat(assetItemDTO1).isEqualTo(assetItemDTO2);
        assetItemDTO2.setId(2L);
        assertThat(assetItemDTO1).isNotEqualTo(assetItemDTO2);
        assetItemDTO1.setId(null);
        assertThat(assetItemDTO1).isNotEqualTo(assetItemDTO2);
    }
}

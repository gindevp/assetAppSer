package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.EquipmentTestSamples.*;
import static com.gindevp.app.domain.ReturnRequestLineTestSamples.*;
import static com.gindevp.app.domain.ReturnRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnRequestLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnRequestLine.class);
        ReturnRequestLine returnRequestLine1 = getReturnRequestLineSample1();
        ReturnRequestLine returnRequestLine2 = new ReturnRequestLine();
        assertThat(returnRequestLine1).isNotEqualTo(returnRequestLine2);

        returnRequestLine2.setId(returnRequestLine1.getId());
        assertThat(returnRequestLine1).isEqualTo(returnRequestLine2);

        returnRequestLine2 = getReturnRequestLineSample2();
        assertThat(returnRequestLine1).isNotEqualTo(returnRequestLine2);
    }

    @Test
    void requestTest() {
        ReturnRequestLine returnRequestLine = getReturnRequestLineRandomSampleGenerator();
        ReturnRequest returnRequestBack = getReturnRequestRandomSampleGenerator();

        returnRequestLine.setRequest(returnRequestBack);
        assertThat(returnRequestLine.getRequest()).isEqualTo(returnRequestBack);

        returnRequestLine.request(null);
        assertThat(returnRequestLine.getRequest()).isNull();
    }

    @Test
    void assetItemTest() {
        ReturnRequestLine returnRequestLine = getReturnRequestLineRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        returnRequestLine.setAssetItem(assetItemBack);
        assertThat(returnRequestLine.getAssetItem()).isEqualTo(assetItemBack);

        returnRequestLine.assetItem(null);
        assertThat(returnRequestLine.getAssetItem()).isNull();
    }

    @Test
    void equipmentTest() {
        ReturnRequestLine returnRequestLine = getReturnRequestLineRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        returnRequestLine.setEquipment(equipmentBack);
        assertThat(returnRequestLine.getEquipment()).isEqualTo(equipmentBack);

        returnRequestLine.equipment(null);
        assertThat(returnRequestLine.getEquipment()).isNull();
    }
}

package com.gindevp.app.domain;

import static com.gindevp.app.domain.AllocationRequestLineTestSamples.*;
import static com.gindevp.app.domain.AllocationRequestTestSamples.*;
import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.AssetLineTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AllocationRequestLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AllocationRequestLine.class);
        AllocationRequestLine allocationRequestLine1 = getAllocationRequestLineSample1();
        AllocationRequestLine allocationRequestLine2 = new AllocationRequestLine();
        assertThat(allocationRequestLine1).isNotEqualTo(allocationRequestLine2);

        allocationRequestLine2.setId(allocationRequestLine1.getId());
        assertThat(allocationRequestLine1).isEqualTo(allocationRequestLine2);

        allocationRequestLine2 = getAllocationRequestLineSample2();
        assertThat(allocationRequestLine1).isNotEqualTo(allocationRequestLine2);
    }

    @Test
    void requestTest() {
        AllocationRequestLine allocationRequestLine = getAllocationRequestLineRandomSampleGenerator();
        AllocationRequest allocationRequestBack = getAllocationRequestRandomSampleGenerator();

        allocationRequestLine.setRequest(allocationRequestBack);
        assertThat(allocationRequestLine.getRequest()).isEqualTo(allocationRequestBack);

        allocationRequestLine.request(null);
        assertThat(allocationRequestLine.getRequest()).isNull();
    }

    @Test
    void assetItemTest() {
        AllocationRequestLine allocationRequestLine = getAllocationRequestLineRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        allocationRequestLine.setAssetItem(assetItemBack);
        assertThat(allocationRequestLine.getAssetItem()).isEqualTo(assetItemBack);

        allocationRequestLine.assetItem(null);
        assertThat(allocationRequestLine.getAssetItem()).isNull();
    }

    @Test
    void assetLineTest() {
        AllocationRequestLine allocationRequestLine = getAllocationRequestLineRandomSampleGenerator();
        AssetLine assetLineBack = getAssetLineRandomSampleGenerator();

        allocationRequestLine.setAssetLine(assetLineBack);
        assertThat(allocationRequestLine.getAssetLine()).isEqualTo(assetLineBack);

        allocationRequestLine.assetLine(null);
        assertThat(allocationRequestLine.getAssetLine()).isNull();
    }
}

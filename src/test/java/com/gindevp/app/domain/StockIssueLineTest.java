package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.EquipmentTestSamples.*;
import static com.gindevp.app.domain.StockIssueLineTestSamples.*;
import static com.gindevp.app.domain.StockIssueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockIssueLineTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockIssueLine.class);
        StockIssueLine stockIssueLine1 = getStockIssueLineSample1();
        StockIssueLine stockIssueLine2 = new StockIssueLine();
        assertThat(stockIssueLine1).isNotEqualTo(stockIssueLine2);

        stockIssueLine2.setId(stockIssueLine1.getId());
        assertThat(stockIssueLine1).isEqualTo(stockIssueLine2);

        stockIssueLine2 = getStockIssueLineSample2();
        assertThat(stockIssueLine1).isNotEqualTo(stockIssueLine2);
    }

    @Test
    void issueTest() {
        StockIssueLine stockIssueLine = getStockIssueLineRandomSampleGenerator();
        StockIssue stockIssueBack = getStockIssueRandomSampleGenerator();

        stockIssueLine.setIssue(stockIssueBack);
        assertThat(stockIssueLine.getIssue()).isEqualTo(stockIssueBack);

        stockIssueLine.issue(null);
        assertThat(stockIssueLine.getIssue()).isNull();
    }

    @Test
    void assetItemTest() {
        StockIssueLine stockIssueLine = getStockIssueLineRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        stockIssueLine.setAssetItem(assetItemBack);
        assertThat(stockIssueLine.getAssetItem()).isEqualTo(assetItemBack);

        stockIssueLine.assetItem(null);
        assertThat(stockIssueLine.getAssetItem()).isNull();
    }

    @Test
    void equipmentTest() {
        StockIssueLine stockIssueLine = getStockIssueLineRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        stockIssueLine.setEquipment(equipmentBack);
        assertThat(stockIssueLine.getEquipment()).isEqualTo(equipmentBack);

        stockIssueLine.equipment(null);
        assertThat(stockIssueLine.getEquipment()).isNull();
    }
}

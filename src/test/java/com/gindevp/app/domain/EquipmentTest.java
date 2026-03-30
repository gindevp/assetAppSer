package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.EquipmentTestSamples.*;
import static com.gindevp.app.domain.SupplierTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Equipment.class);
        Equipment equipment1 = getEquipmentSample1();
        Equipment equipment2 = new Equipment();
        assertThat(equipment1).isNotEqualTo(equipment2);

        equipment2.setId(equipment1.getId());
        assertThat(equipment1).isEqualTo(equipment2);

        equipment2 = getEquipmentSample2();
        assertThat(equipment1).isNotEqualTo(equipment2);
    }

    @Test
    void assetItemTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        equipment.setAssetItem(assetItemBack);
        assertThat(equipment.getAssetItem()).isEqualTo(assetItemBack);

        equipment.assetItem(null);
        assertThat(equipment.getAssetItem()).isNull();
    }

    @Test
    void supplierTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        Supplier supplierBack = getSupplierRandomSampleGenerator();

        equipment.setSupplier(supplierBack);
        assertThat(equipment.getSupplier()).isEqualTo(supplierBack);

        equipment.supplier(null);
        assertThat(equipment.getSupplier()).isNull();
    }
}

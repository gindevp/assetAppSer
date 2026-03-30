package com.gindevp.app.domain;

import static com.gindevp.app.domain.EmployeeTestSamples.*;
import static com.gindevp.app.domain.EquipmentTestSamples.*;
import static com.gindevp.app.domain.RepairRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RepairRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RepairRequest.class);
        RepairRequest repairRequest1 = getRepairRequestSample1();
        RepairRequest repairRequest2 = new RepairRequest();
        assertThat(repairRequest1).isNotEqualTo(repairRequest2);

        repairRequest2.setId(repairRequest1.getId());
        assertThat(repairRequest1).isEqualTo(repairRequest2);

        repairRequest2 = getRepairRequestSample2();
        assertThat(repairRequest1).isNotEqualTo(repairRequest2);
    }

    @Test
    void requesterTest() {
        RepairRequest repairRequest = getRepairRequestRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        repairRequest.setRequester(employeeBack);
        assertThat(repairRequest.getRequester()).isEqualTo(employeeBack);

        repairRequest.requester(null);
        assertThat(repairRequest.getRequester()).isNull();
    }

    @Test
    void equipmentTest() {
        RepairRequest repairRequest = getRepairRequestRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        repairRequest.setEquipment(equipmentBack);
        assertThat(repairRequest.getEquipment()).isEqualTo(equipmentBack);

        repairRequest.equipment(null);
        assertThat(repairRequest.getEquipment()).isNull();
    }
}

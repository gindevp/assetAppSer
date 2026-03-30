package com.gindevp.app.domain;

import static com.gindevp.app.domain.AssetItemTestSamples.*;
import static com.gindevp.app.domain.ConsumableAssignmentTestSamples.*;
import static com.gindevp.app.domain.DepartmentTestSamples.*;
import static com.gindevp.app.domain.EmployeeTestSamples.*;
import static com.gindevp.app.domain.LocationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ConsumableAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ConsumableAssignment.class);
        ConsumableAssignment consumableAssignment1 = getConsumableAssignmentSample1();
        ConsumableAssignment consumableAssignment2 = new ConsumableAssignment();
        assertThat(consumableAssignment1).isNotEqualTo(consumableAssignment2);

        consumableAssignment2.setId(consumableAssignment1.getId());
        assertThat(consumableAssignment1).isEqualTo(consumableAssignment2);

        consumableAssignment2 = getConsumableAssignmentSample2();
        assertThat(consumableAssignment1).isNotEqualTo(consumableAssignment2);
    }

    @Test
    void assetItemTest() {
        ConsumableAssignment consumableAssignment = getConsumableAssignmentRandomSampleGenerator();
        AssetItem assetItemBack = getAssetItemRandomSampleGenerator();

        consumableAssignment.setAssetItem(assetItemBack);
        assertThat(consumableAssignment.getAssetItem()).isEqualTo(assetItemBack);

        consumableAssignment.assetItem(null);
        assertThat(consumableAssignment.getAssetItem()).isNull();
    }

    @Test
    void employeeTest() {
        ConsumableAssignment consumableAssignment = getConsumableAssignmentRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        consumableAssignment.setEmployee(employeeBack);
        assertThat(consumableAssignment.getEmployee()).isEqualTo(employeeBack);

        consumableAssignment.employee(null);
        assertThat(consumableAssignment.getEmployee()).isNull();
    }

    @Test
    void departmentTest() {
        ConsumableAssignment consumableAssignment = getConsumableAssignmentRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        consumableAssignment.setDepartment(departmentBack);
        assertThat(consumableAssignment.getDepartment()).isEqualTo(departmentBack);

        consumableAssignment.department(null);
        assertThat(consumableAssignment.getDepartment()).isNull();
    }

    @Test
    void locationTest() {
        ConsumableAssignment consumableAssignment = getConsumableAssignmentRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        consumableAssignment.setLocation(locationBack);
        assertThat(consumableAssignment.getLocation()).isEqualTo(locationBack);

        consumableAssignment.location(null);
        assertThat(consumableAssignment.getLocation()).isNull();
    }
}

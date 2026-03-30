package com.gindevp.app.domain;

import static com.gindevp.app.domain.DepartmentTestSamples.*;
import static com.gindevp.app.domain.EmployeeTestSamples.*;
import static com.gindevp.app.domain.EquipmentAssignmentTestSamples.*;
import static com.gindevp.app.domain.EquipmentTestSamples.*;
import static com.gindevp.app.domain.LocationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EquipmentAssignment.class);
        EquipmentAssignment equipmentAssignment1 = getEquipmentAssignmentSample1();
        EquipmentAssignment equipmentAssignment2 = new EquipmentAssignment();
        assertThat(equipmentAssignment1).isNotEqualTo(equipmentAssignment2);

        equipmentAssignment2.setId(equipmentAssignment1.getId());
        assertThat(equipmentAssignment1).isEqualTo(equipmentAssignment2);

        equipmentAssignment2 = getEquipmentAssignmentSample2();
        assertThat(equipmentAssignment1).isNotEqualTo(equipmentAssignment2);
    }

    @Test
    void equipmentTest() {
        EquipmentAssignment equipmentAssignment = getEquipmentAssignmentRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        equipmentAssignment.setEquipment(equipmentBack);
        assertThat(equipmentAssignment.getEquipment()).isEqualTo(equipmentBack);

        equipmentAssignment.equipment(null);
        assertThat(equipmentAssignment.getEquipment()).isNull();
    }

    @Test
    void employeeTest() {
        EquipmentAssignment equipmentAssignment = getEquipmentAssignmentRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        equipmentAssignment.setEmployee(employeeBack);
        assertThat(equipmentAssignment.getEmployee()).isEqualTo(employeeBack);

        equipmentAssignment.employee(null);
        assertThat(equipmentAssignment.getEmployee()).isNull();
    }

    @Test
    void departmentTest() {
        EquipmentAssignment equipmentAssignment = getEquipmentAssignmentRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        equipmentAssignment.setDepartment(departmentBack);
        assertThat(equipmentAssignment.getDepartment()).isEqualTo(departmentBack);

        equipmentAssignment.department(null);
        assertThat(equipmentAssignment.getDepartment()).isNull();
    }

    @Test
    void locationTest() {
        EquipmentAssignment equipmentAssignment = getEquipmentAssignmentRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        equipmentAssignment.setLocation(locationBack);
        assertThat(equipmentAssignment.getLocation()).isEqualTo(locationBack);

        equipmentAssignment.location(null);
        assertThat(equipmentAssignment.getLocation()).isNull();
    }
}

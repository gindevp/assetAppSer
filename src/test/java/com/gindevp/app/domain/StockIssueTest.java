package com.gindevp.app.domain;

import static com.gindevp.app.domain.DepartmentTestSamples.*;
import static com.gindevp.app.domain.EmployeeTestSamples.*;
import static com.gindevp.app.domain.LocationTestSamples.*;
import static com.gindevp.app.domain.StockIssueTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockIssueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockIssue.class);
        StockIssue stockIssue1 = getStockIssueSample1();
        StockIssue stockIssue2 = new StockIssue();
        assertThat(stockIssue1).isNotEqualTo(stockIssue2);

        stockIssue2.setId(stockIssue1.getId());
        assertThat(stockIssue1).isEqualTo(stockIssue2);

        stockIssue2 = getStockIssueSample2();
        assertThat(stockIssue1).isNotEqualTo(stockIssue2);
    }

    @Test
    void employeeTest() {
        StockIssue stockIssue = getStockIssueRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        stockIssue.setEmployee(employeeBack);
        assertThat(stockIssue.getEmployee()).isEqualTo(employeeBack);

        stockIssue.employee(null);
        assertThat(stockIssue.getEmployee()).isNull();
    }

    @Test
    void departmentTest() {
        StockIssue stockIssue = getStockIssueRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        stockIssue.setDepartment(departmentBack);
        assertThat(stockIssue.getDepartment()).isEqualTo(departmentBack);

        stockIssue.department(null);
        assertThat(stockIssue.getDepartment()).isNull();
    }

    @Test
    void locationTest() {
        StockIssue stockIssue = getStockIssueRandomSampleGenerator();
        Location locationBack = getLocationRandomSampleGenerator();

        stockIssue.setLocation(locationBack);
        assertThat(stockIssue.getLocation()).isEqualTo(locationBack);

        stockIssue.location(null);
        assertThat(stockIssue.getLocation()).isNull();
    }
}

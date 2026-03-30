package com.gindevp.app.domain;

import static com.gindevp.app.domain.AllocationRequestTestSamples.*;
import static com.gindevp.app.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AllocationRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AllocationRequest.class);
        AllocationRequest allocationRequest1 = getAllocationRequestSample1();
        AllocationRequest allocationRequest2 = new AllocationRequest();
        assertThat(allocationRequest1).isNotEqualTo(allocationRequest2);

        allocationRequest2.setId(allocationRequest1.getId());
        assertThat(allocationRequest1).isEqualTo(allocationRequest2);

        allocationRequest2 = getAllocationRequestSample2();
        assertThat(allocationRequest1).isNotEqualTo(allocationRequest2);
    }

    @Test
    void requesterTest() {
        AllocationRequest allocationRequest = getAllocationRequestRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        allocationRequest.setRequester(employeeBack);
        assertThat(allocationRequest.getRequester()).isEqualTo(employeeBack);

        allocationRequest.requester(null);
        assertThat(allocationRequest.getRequester()).isNull();
    }
}

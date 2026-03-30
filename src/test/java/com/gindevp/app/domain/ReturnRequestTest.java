package com.gindevp.app.domain;

import static com.gindevp.app.domain.EmployeeTestSamples.*;
import static com.gindevp.app.domain.ReturnRequestTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.gindevp.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReturnRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ReturnRequest.class);
        ReturnRequest returnRequest1 = getReturnRequestSample1();
        ReturnRequest returnRequest2 = new ReturnRequest();
        assertThat(returnRequest1).isNotEqualTo(returnRequest2);

        returnRequest2.setId(returnRequest1.getId());
        assertThat(returnRequest1).isEqualTo(returnRequest2);

        returnRequest2 = getReturnRequestSample2();
        assertThat(returnRequest1).isNotEqualTo(returnRequest2);
    }

    @Test
    void requesterTest() {
        ReturnRequest returnRequest = getReturnRequestRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        returnRequest.setRequester(employeeBack);
        assertThat(returnRequest.getRequester()).isEqualTo(employeeBack);

        returnRequest.requester(null);
        assertThat(returnRequest.getRequester()).isNull();
    }
}

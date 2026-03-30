package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AllocationRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AllocationRequest getAllocationRequestSample1() {
        return new AllocationRequest().id(1L).code("code1").reason("reason1").beneficiaryNote("beneficiaryNote1");
    }

    public static AllocationRequest getAllocationRequestSample2() {
        return new AllocationRequest().id(2L).code("code2").reason("reason2").beneficiaryNote("beneficiaryNote2");
    }

    public static AllocationRequest getAllocationRequestRandomSampleGenerator() {
        return new AllocationRequest()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .reason(UUID.randomUUID().toString())
            .beneficiaryNote(UUID.randomUUID().toString());
    }
}

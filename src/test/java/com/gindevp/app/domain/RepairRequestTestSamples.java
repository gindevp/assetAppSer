package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RepairRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RepairRequest getRepairRequestSample1() {
        return new RepairRequest()
            .id(1L)
            .code("code1")
            .problemCategory("problemCategory1")
            .description("description1")
            .resolutionNote("resolutionNote1");
    }

    public static RepairRequest getRepairRequestSample2() {
        return new RepairRequest()
            .id(2L)
            .code("code2")
            .problemCategory("problemCategory2")
            .description("description2")
            .resolutionNote("resolutionNote2");
    }

    public static RepairRequest getRepairRequestRandomSampleGenerator() {
        return new RepairRequest()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .problemCategory(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .resolutionNote(UUID.randomUUID().toString());
    }
}

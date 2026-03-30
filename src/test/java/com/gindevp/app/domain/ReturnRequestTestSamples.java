package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ReturnRequestTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ReturnRequest getReturnRequestSample1() {
        return new ReturnRequest().id(1L).code("code1").note("note1");
    }

    public static ReturnRequest getReturnRequestSample2() {
        return new ReturnRequest().id(2L).code("code2").note("note2");
    }

    public static ReturnRequest getReturnRequestRandomSampleGenerator() {
        return new ReturnRequest().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).note(UUID.randomUUID().toString());
    }
}

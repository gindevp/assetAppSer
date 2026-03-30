package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AllocationRequestLineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static AllocationRequestLine getAllocationRequestLineSample1() {
        return new AllocationRequestLine().id(1L).lineNo(1).quantity(1).note("note1");
    }

    public static AllocationRequestLine getAllocationRequestLineSample2() {
        return new AllocationRequestLine().id(2L).lineNo(2).quantity(2).note("note2");
    }

    public static AllocationRequestLine getAllocationRequestLineRandomSampleGenerator() {
        return new AllocationRequestLine()
            .id(longCount.incrementAndGet())
            .lineNo(intCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}

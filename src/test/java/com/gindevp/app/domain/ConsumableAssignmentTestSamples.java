package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumableAssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ConsumableAssignment getConsumableAssignmentSample1() {
        return new ConsumableAssignment().id(1L).quantity(1).returnedQuantity(1).note("note1");
    }

    public static ConsumableAssignment getConsumableAssignmentSample2() {
        return new ConsumableAssignment().id(2L).quantity(2).returnedQuantity(2).note("note2");
    }

    public static ConsumableAssignment getConsumableAssignmentRandomSampleGenerator() {
        return new ConsumableAssignment()
            .id(longCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .returnedQuantity(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}

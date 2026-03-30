package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsumableStockTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ConsumableStock getConsumableStockSample1() {
        return new ConsumableStock().id(1L).quantityOnHand(1).quantityIssued(1).note("note1");
    }

    public static ConsumableStock getConsumableStockSample2() {
        return new ConsumableStock().id(2L).quantityOnHand(2).quantityIssued(2).note("note2");
    }

    public static ConsumableStock getConsumableStockRandomSampleGenerator() {
        return new ConsumableStock()
            .id(longCount.incrementAndGet())
            .quantityOnHand(intCount.incrementAndGet())
            .quantityIssued(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}

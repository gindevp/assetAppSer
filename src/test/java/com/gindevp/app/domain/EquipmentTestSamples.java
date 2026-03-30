package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EquipmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Equipment getEquipmentSample1() {
        return new Equipment()
            .id(1L)
            .equipmentCode("equipmentCode1")
            .serial("serial1")
            .conditionNote("conditionNote1")
            .depreciationMonths(1);
    }

    public static Equipment getEquipmentSample2() {
        return new Equipment()
            .id(2L)
            .equipmentCode("equipmentCode2")
            .serial("serial2")
            .conditionNote("conditionNote2")
            .depreciationMonths(2);
    }

    public static Equipment getEquipmentRandomSampleGenerator() {
        return new Equipment()
            .id(longCount.incrementAndGet())
            .equipmentCode(UUID.randomUUID().toString())
            .serial(UUID.randomUUID().toString())
            .conditionNote(UUID.randomUUID().toString())
            .depreciationMonths(intCount.incrementAndGet());
    }
}

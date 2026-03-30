package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EquipmentAssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EquipmentAssignment getEquipmentAssignmentSample1() {
        return new EquipmentAssignment().id(1L).note("note1");
    }

    public static EquipmentAssignment getEquipmentAssignmentSample2() {
        return new EquipmentAssignment().id(2L).note("note2");
    }

    public static EquipmentAssignment getEquipmentAssignmentRandomSampleGenerator() {
        return new EquipmentAssignment().id(longCount.incrementAndGet()).note(UUID.randomUUID().toString());
    }
}

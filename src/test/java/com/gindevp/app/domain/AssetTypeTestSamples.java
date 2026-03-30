package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssetTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AssetType getAssetTypeSample1() {
        return new AssetType().id(1L).code("code1").name("name1").description("description1");
    }

    public static AssetType getAssetTypeSample2() {
        return new AssetType().id(2L).code("code2").name("name2").description("description2");
    }

    public static AssetType getAssetTypeRandomSampleGenerator() {
        return new AssetType()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}

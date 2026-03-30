package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssetGroupTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AssetGroup getAssetGroupSample1() {
        return new AssetGroup().id(1L).code("code1").name("name1").description("description1");
    }

    public static AssetGroup getAssetGroupSample2() {
        return new AssetGroup().id(2L).code("code2").name("name2").description("description2");
    }

    public static AssetGroup getAssetGroupRandomSampleGenerator() {
        return new AssetGroup()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}

package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AssetItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AssetItem getAssetItemSample1() {
        return new AssetItem().id(1L).code("code1").name("name1").unit("unit1").note("note1");
    }

    public static AssetItem getAssetItemSample2() {
        return new AssetItem().id(2L).code("code2").name("name2").unit("unit2").note("note2");
    }

    public static AssetItem getAssetItemRandomSampleGenerator() {
        return new AssetItem()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .unit(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}

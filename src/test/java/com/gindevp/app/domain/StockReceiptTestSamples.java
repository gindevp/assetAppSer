package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockReceiptTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockReceipt getStockReceiptSample1() {
        return new StockReceipt().id(1L).code("code1").note("note1");
    }

    public static StockReceipt getStockReceiptSample2() {
        return new StockReceipt().id(2L).code("code2").note("note2");
    }

    public static StockReceipt getStockReceiptRandomSampleGenerator() {
        return new StockReceipt().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).note(UUID.randomUUID().toString());
    }
}

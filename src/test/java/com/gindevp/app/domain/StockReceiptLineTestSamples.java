package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class StockReceiptLineTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static StockReceiptLine getStockReceiptLineSample1() {
        return new StockReceiptLine().id(1L).lineNo(1).quantity(1).note("note1");
    }

    public static StockReceiptLine getStockReceiptLineSample2() {
        return new StockReceiptLine().id(2L).lineNo(2).quantity(2).note("note2");
    }

    public static StockReceiptLine getStockReceiptLineRandomSampleGenerator() {
        return new StockReceiptLine()
            .id(longCount.incrementAndGet())
            .lineNo(intCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}

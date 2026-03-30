package com.gindevp.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockIssueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockIssue getStockIssueSample1() {
        return new StockIssue().id(1L).code("code1").note("note1");
    }

    public static StockIssue getStockIssueSample2() {
        return new StockIssue().id(2L).code("code2").note("note2");
    }

    public static StockIssue getStockIssueRandomSampleGenerator() {
        return new StockIssue().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).note(UUID.randomUUID().toString());
    }
}

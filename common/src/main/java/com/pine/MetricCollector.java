package com.pine;

import java.util.HashMap;
import java.util.Map;

public abstract class MetricCollector {
    private static final Map<String, Long> metrics = new HashMap<>();
    private long start;

    protected void start() {
        start = System.currentTimeMillis();
    }

    protected void end() {
        metrics.put(getTitle(), System.currentTimeMillis() - start);
    }

    public static Map<String, Long> getMetrics() {
        return metrics;
    }

    public abstract String getTitle();
}

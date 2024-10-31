package com.pine;

import java.util.HashMap;
import java.util.Map;

public abstract class MetricCollector {
    public static boolean shouldCollect = false;
    private static final Map<String, Long> metrics = new HashMap<>();
    private long start;

    protected void startTracking() {
        if (shouldCollect) {
            start = System.nanoTime();
        }
    }

    protected void endTracking() {
        if(!metrics.containsKey(getTitle())){
            metrics.put(getTitle(), 0L);
        }
        if (shouldCollect) {
            metrics.put(getTitle(), System.nanoTime() - start);
        }
    }

    public static Map<String, Long> getMetrics() {
        return metrics;
    }

    public abstract String getTitle();
}

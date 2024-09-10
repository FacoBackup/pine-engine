package com.pine.engine.core;

import com.pine.common.EngineComponent;

public class ClockRepository implements EngineComponent {
    public final long startupTime = System.currentTimeMillis();
    public long since = 0;
    public long elapsedTime = 0;
    public long totalTime = 0;

    @Override
    public void tick() {
        long newSince = System.currentTimeMillis();
        totalTime = newSince - startupTime;
        elapsedTime += newSince - since;
        since = newSince;
    }
}
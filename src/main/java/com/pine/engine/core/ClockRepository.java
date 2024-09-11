package com.pine.engine.core;

import com.pine.common.EngineComponent;
import com.pine.engine.Engine;
import com.pine.engine.core.service.EngineInjectable;

public class ClockRepository implements EngineInjectable, EngineComponent {
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

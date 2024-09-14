package com.pine.engine.core.repository;

import com.pine.common.Updatable;
import com.pine.engine.core.EngineInjectable;

@EngineInjectable
public class ClockRepository implements Updatable {
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

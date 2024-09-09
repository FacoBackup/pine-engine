package com.pine.engine.core.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractMultithreadedSystem extends AbstractSystem {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean isInitiated;

    protected int getTickIntervalMilliseconds() {
        return 16;
    }

    @Override
    final public void render() {
    }

    @Override
    final public void tick() {
        if (!isInitiated) {
            isInitiated = true;
            scheduler.scheduleAtFixedRate(this::tickInternal, 0, getTickIntervalMilliseconds(), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Do work on a separated thread
     */
    protected abstract void tickInternal();
}

package com.pine.tasks;

import com.pine.LateInitializable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTask implements LateInitializable {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected int getTickIntervalMilliseconds() {
        return 16;
    }

    @Override
    public void lateInitialize() {
        scheduler.scheduleAtFixedRate(this::tickInternal, 0, getTickIntervalMilliseconds(), TimeUnit.MILLISECONDS);
    }

    /**
     * Do work on a separated thread
     */
    protected abstract void tickInternal();
}

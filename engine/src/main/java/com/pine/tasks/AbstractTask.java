package com.pine.tasks;

import com.pine.injection.Disposable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTask implements Disposable {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    protected int getTickIntervalMilliseconds() {
        return 16;
    }

    final public void start() {
        scheduler.scheduleAtFixedRate(this::tickInternal, 0, getTickIntervalMilliseconds(), TimeUnit.MILLISECONDS);
    }

    /**
     * Do work on a separated thread
     */
    protected abstract void tickInternal();

    @Override
    public void dispose() {
        scheduler.shutdown();
    }
}

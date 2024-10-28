package com.pine.tasks;

import com.pine.MetricCollector;
import com.pine.injection.Disposable;
import com.pine.messaging.Loggable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTask extends MetricCollector implements Loggable, Disposable {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean started;

    protected int getTickIntervalMilliseconds() {
        return 16;
    }

    final public void start() {
        if(started){
            getLogger().error("Thread already started");
            return;
        }
        started = true;
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

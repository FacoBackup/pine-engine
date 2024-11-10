package com.pine.tasks;

import com.pine.MetricCollector;
import com.pine.injection.Disposable;
import com.pine.messaging.Loggable;

public abstract class AbstractTask extends MetricCollector implements Loggable, Disposable {
    private boolean started;
    private final Thread thread = new Thread(this::runInternal);

    protected long getInterval(){
        return 16;
    }

    private void runInternal() {
        while (true) {
            tickInternal();
            try {
                Thread.sleep(getInterval());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    final public void start() {
        if (started) {
            getLogger().error("Thread already started");
            return;
        }
        started = true;
        thread.start();
    }

    /**
     * Do work on a separated thread
     */
    protected abstract void tickInternal();

    @Override
    public void dispose() {
        thread.interrupt();
    }
}

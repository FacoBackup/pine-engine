package com.pine.engine.core.system;

import com.pine.engine.Engine;

public abstract class AbstractMultithreadedSystem extends AbstractSystem {
    private final Thread thread = new Thread(this::tickInternal);

    @Override
    final public void process() {
        // ONLY FOR RENDERING
    }

    @Override
    final public void tick() {
        thread.start();
    }

    /**
     * Do work on a separated thread
     */
    protected abstract void tickInternal();
}

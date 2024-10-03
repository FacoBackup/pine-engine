package com.pine.tasks;

import com.pine.injection.PBean;

@PBean
public class PhysicsTask extends AbstractTask {
    @Override
    protected int getTickIntervalMilliseconds() {
        return super.getTickIntervalMilliseconds(); // TODO - GET PROPERTY
    }

    @Override
    protected void tickInternal() {
        // TODO - World tick
    }
}

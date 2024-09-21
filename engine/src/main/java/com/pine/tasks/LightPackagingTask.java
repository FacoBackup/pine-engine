package com.pine.tasks;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.component.AtmosphereComponent;
import com.pine.component.LightComponent;
import com.pine.repository.CoreResourceRepository;

@PBean
public class LightPackagingTask extends AbstractTask {
    @PInject
    public CoreResourceRepository coreResourceRepository;

    @PInject
    public LightComponent lights;

    @PInject
    public AtmosphereComponent atmospheres;

    @Override
    protected int getTickIntervalMilliseconds() {
        return 1000; // 1 time per second
    }

    @Override
    protected void tickInternal() {

    }
}

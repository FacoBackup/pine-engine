package com.pine.tasks;

import com.pine.injection.EngineDependency;
import com.pine.injection.EngineInjectable;
import com.pine.repository.CoreResourceRepository;
import com.pine.service.world.WorldService;

@EngineInjectable
public class LightPackagingTask extends AbstractTask {
    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
    public WorldService worldService;

    @Override
    protected int getTickIntervalMilliseconds() {
        return 1000; // 1 time per second
    }

    @Override
    protected void tickInternal() {

    }
}
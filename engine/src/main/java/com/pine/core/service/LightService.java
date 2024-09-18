package com.pine.core.service;

import com.pine.core.EngineDependency;
import com.pine.core.EngineInjectable;
import com.pine.core.repository.CoreResourceRepository;
import com.pine.core.service.world.WorldService;

@EngineInjectable
public class LightService extends AbstractMultithreadedService {
    @EngineDependency
    public CoreResourceRepository coreResourceRepository;

    @EngineDependency
    public WorldService worldService;

    @Override
    protected int getTickIntervalMilliseconds() {
        return 32; // 30 fps
    }

    @Override
    protected void tickInternal() {

    }
}

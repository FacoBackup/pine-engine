package com.pine.engine.core.service;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.repository.CoreResourceRepository;
import com.pine.engine.core.service.world.WorldService;

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

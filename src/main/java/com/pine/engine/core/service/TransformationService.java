package com.pine.engine.core.service;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.service.world.WorldService;

@EngineInjectable
public class TransformationService extends AbstractMultithreadedService{

    @EngineDependency
    public WorldService worldService;

    @Override
    protected void tickInternal() {

    }
}

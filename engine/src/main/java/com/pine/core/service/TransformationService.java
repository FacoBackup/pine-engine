package com.pine.core.service;

import com.pine.core.EngineDependency;
import com.pine.core.EngineInjectable;
import com.pine.core.service.world.WorldService;

@EngineInjectable
public class TransformationService extends AbstractMultithreadedService{

    @EngineDependency
    public WorldService worldService;

    @Override
    protected void tickInternal() {

    }
}

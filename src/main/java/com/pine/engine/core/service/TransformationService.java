package com.pine.engine.core.service;

import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.service.entity.EntityService;

@EngineInjectable
public class TransformationService extends AbstractMultithreadedService{

    @EngineDependency
    public EntityService entityService;

    @Override
    protected void tickInternal() {

    }
}

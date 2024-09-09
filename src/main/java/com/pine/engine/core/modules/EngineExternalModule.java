package com.pine.engine.core.modules;

import com.pine.engine.core.system.ISystem;

import java.util.List;

public class EngineExternalModule<T extends IExternalService> {
    private final List<ISystem> systems;
    private final T service;

    public EngineExternalModule(T service, List<ISystem> systems) {
        this.service = service;
        this.systems = systems;
    }

    public List<ISystem> getSystems() {
        return systems;
    }

    public T getService() {
        return service;
    }
}

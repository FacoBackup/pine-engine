package com.pine.engine.core.service.system;

import com.pine.common.Updatable;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.EngineInjector;
import com.pine.engine.core.LateInitializable;
import com.pine.engine.core.service.system.impl.*;

import java.util.List;

@EngineInjectable
public class SystemService implements LateInitializable, Updatable {
    @EngineDependency
    public EngineInjector engineInjector;

    private List<AbstractSystem> systems = List.of(
            new UBOSyncSystem(),
            new InstancedRenderingSystem()
    );

    public List<AbstractSystem> getSystems() {
        return systems;
    }

    public void setSystems(List<AbstractSystem> systems) {
        for (var sys : systems) {
            if (!this.systems.contains(sys)) {
                engineInjector.inject(sys);
                sys.onInitialize();
            }
        }
        this.systems = systems;
    }

    @Override
    public void tick() {
        for (var system : systems) {
            system.render();
        }
    }

    @Override
    public void lateInitialize() {
        for (var sys : systems) {
            engineInjector.inject(sys);
            sys.onInitialize();
        }
    }
}
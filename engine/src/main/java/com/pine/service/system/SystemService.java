package com.pine.service.system;

import com.pine.Updatable;
import com.pine.annotation.EngineDependency;
import com.pine.annotation.EngineInjectable;
import com.pine.annotation.EngineInjector;
import com.pine.service.system.impl.*;

import java.util.List;

@EngineInjectable
public class SystemService implements Updatable {
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


    public void manualInitialization() {
        for (var sys : systems) {
            engineInjector.inject(sys);
            sys.onInitialize();
        }
    }
}
package com.pine.service.system;

import com.pine.PBean;
import com.pine.PInject;
import com.pine.PInjector;
import com.pine.Updatable;
import com.pine.service.system.impl.DemoRenderSystem;
import com.pine.service.system.impl.InstancedRenderingSystem;
import com.pine.service.system.impl.UBOSyncSystem;

import java.util.List;

@PBean
public class SystemService implements Updatable {
    @PInject
    public PInjector pInjector;

    private List<AbstractSystem> systems = List.of(
            new UBOSyncSystem(),
            new DemoRenderSystem()
    );

    public List<AbstractSystem> getSystems() {
        return systems;
    }

    public void setSystems(List<AbstractSystem> systems) {
        for (var sys : systems) {
            if (!this.systems.contains(sys)) {
                pInjector.inject(sys);
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


    public void initialize() {
        for (var sys : systems) {
            pInjector.inject(sys);
            sys.onInitialize();
        }
    }
}
package com.pine.service.system;

import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.injection.PInjector;
import com.pine.service.system.impl.*;
import com.pine.tasks.SyncTask;

import java.util.List;

@PBean
public class SystemService implements SyncTask {
    @PInject
    public PInjector pInjector;

    private List<AbstractSystem> systems = List.of(
            new BRDFGenSystem(),
            new ShaderDataSyncSystem(),
            new VoxelizationSystem(),
            new DepthPrePassSystem(),
            new AtmosphereSystem(),
            new VoxelVisualizerSystem(),
            new FrameCompositionSystem()
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
    public void sync() {
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
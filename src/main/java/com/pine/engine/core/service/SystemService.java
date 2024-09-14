package com.pine.engine.core.service;

import com.pine.common.Updatable;
import com.pine.common.Renderable;
import com.pine.engine.core.EngineInjector;
import com.pine.engine.core.LateInitializable;
import com.pine.engine.core.system.AbstractSystem;
import com.pine.engine.core.EngineDependency;
import com.pine.engine.core.EngineInjectable;
import com.pine.engine.core.system.impl.*;

import java.util.List;

@EngineInjectable
public class SystemService implements LateInitializable, Updatable, Renderable {
    @EngineDependency
    public EngineInjector engineInjector;

    private List<AbstractSystem> systems = List.of(
            new PreLoopSystem(),
            new ScriptExecutorSystem(),
            new ShadowsSystem(),
            new VisibilityRendererSystem(),
            new PreRendererSystem(),
            new AtmosphereRendererSystem(),
            new TerrainRendererSystem(),
            new OpaqueRendererSystem(),
            new DecalRendererSystem(),
            new SpriteRendererSystem(),
            new PostRendererSystem(),
            new TransparencyRendererSystem(),
            new GlobalIlluminationSystem(),
            new PostProcessingSystem(),
            new FrameCompositionSystem()
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
            system.tick();
        }
    }

    @Override
    public void render() {
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
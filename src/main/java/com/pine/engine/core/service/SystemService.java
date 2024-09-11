package com.pine.engine.core.service;

import com.pine.common.EngineComponent;
import com.pine.common.Renderable;
import com.pine.engine.Engine;
import com.pine.engine.core.CoreResourceRepository;
import com.pine.engine.core.system.InjectEngineDependency;
import com.pine.engine.core.ClockRepository;
import com.pine.engine.core.RuntimeRepository;
import com.pine.engine.core.modules.EngineExternalModule;
import com.pine.engine.core.service.camera.CameraService;
import com.pine.engine.core.service.entity.EntityService;
import com.pine.engine.core.service.loader.ResourceLoaderService;
import com.pine.engine.core.service.resource.ResourceService;
import com.pine.engine.core.system.ISystem;
import com.pine.engine.core.system.impl.*;
import com.pine.engine.tools.system.GridSystem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SystemService implements EngineComponent, EngineInjectable, Renderable {
    private final Engine engine;
    private final Collection<EngineExternalModule> modules;
    private List<ISystem> systems = List.of(
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
    private final List<EngineInjectable> injectables = new ArrayList<>();

    public SystemService(Engine engine, Map<String, EngineExternalModule> modules) {
        this.engine = engine;
        this.modules = modules.values();
    }

    @Override
    public void onInitialize() {
        injectables.add(engine.getClock());
        injectables.add(engine.getRuntimeRepository());
        injectables.add(engine.getEntityService());
        injectables.add(engine.getCameraService());
        injectables.add(engine.getSystemsService());
        injectables.add(engine.getResourcesService());
        injectables.add(engine.getCoreResourceRepository());
        injectables.add(engine.getResourceLoaderService());
        injectables.addAll(engine.getResourcesService().getImplementations());

        initializeModules();
        for (var sys : systems) {
            injectSystemDependencies(sys);
            sys.onInitialize();
        }
    }

    private void initializeModules() {
        List<EngineInjectable> newInjectables = new ArrayList<>();
        for (var externalSystem : modules) {
            systems = externalSystem.getExternalSystems(systems);
            newInjectables.addAll(externalSystem.getInjectables());
        }

        for (var injectable : newInjectables) {
            injectable.setEngine(engine);
        }

        // FOR CIRCULAR DEPENDENCY CASES
        for (var injectable : newInjectables) {
            injectable.onInitialize();
        }

        injectables.addAll(newInjectables);
    }

    private void injectSystemDependencies(ISystem system) {
        Field[] fields = system.getClass().getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectEngineDependency.class)) {
                boolean isInjected = false;
                for (EngineInjectable i : injectables) {
                    if (i.getClass() == field.getType()) {
                        inject(system, field, i);
                        isInjected = true;
                    }
                }
                if (!isInjected) {
                    getLogger().warn("Trying module injection on class {}", system.getClass().getName());
                    for (var module : modules) {
                        if (field.getType() == module.getClass()) {
                            inject(system, field, module);
                        }
                    }
                }
            }
        }
    }

    private void inject(ISystem system, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(system, value);
        } catch (Exception e) {
            getLogger().error("Failed to inject dependency", e);
        }
    }

    @Override
    public void tick() {
        for (ISystem system : systems) {
            system.tick();
        }
    }

    @Override
    public void render() {
        for (ISystem system : systems) {
            system.render();
        }
    }
}